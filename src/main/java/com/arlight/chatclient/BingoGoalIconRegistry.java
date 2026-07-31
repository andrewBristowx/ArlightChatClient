package com.arlight.chatclient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

/** Resuelve un objetivo a ItemStack o a un icono PNG exclusivo del Bingo. */
public final class BingoGoalIconRegistry {
    public record Icon(ItemStack stack, ResourceLocation texture) {
        public boolean isTexture() { return texture != null; }
    }

    private static final Map<String, ResourceLocation> CUSTOM = Map.ofEntries(
            entry("boss_surface"), entry("boss_nether"), entry("boss_dragon"), entry("boss_void"),
            entry("emerald_zombie"), entry("emerald_creeper"), entry("gilded_piglin"),
            entry("gilded_hoglin"), entry("amethyst_eye"), entry("void_enderman"),
            entry("amethyst_shulker"), entry("unknown")
    );

    private BingoGoalIconRegistry() { }

    private static Map.Entry<String, ResourceLocation> entry(String id) {
        return Map.entry(id, ResourceLocation.fromNamespaceAndPath(
                ArlightChatClient.MOD_ID, "textures/bingo/icons/" + id + ".png"));
    }

    public static Icon resolve(BingoCardOverlay.GoalData goal) {
        if (goal == null) return custom("unknown");
        String explicit = normalize(goal.iconKey());
        if (!explicit.isBlank() && CUSTOM.containsKey(explicit)) return custom(explicit);

        String target = normalize(goal.target());
        String inferred = inferCustom(target, normalize(goal.id()));
        if (!inferred.isBlank()) return custom(inferred);

        ResourceLocation id = ResourceLocation.tryParse(goal.target());
        if ("ENTITY_KILL".equals(goal.type()) && id != null) {
            ResourceLocation eggId = ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(), id.getPath() + "_spawn_egg");
            Item egg = BuiltInRegistries.ITEM.get(eggId);
            if (egg != null && egg != Items.AIR) return new Icon(new ItemStack(egg), null);
        }
        if (id != null) {
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item != null && item != Items.AIR) return new Icon(new ItemStack(item), null);
        }

        return switch (goal.type()) {
            case "ENTITY_KILL" -> new Icon(new ItemStack(Items.IRON_SWORD), null);
            case "ADVANCEMENT" -> new Icon(new ItemStack(Items.KNOWLEDGE_BOOK), null);
            case "CUSTOM_TRIGGER" -> custom("unknown");
            default -> new Icon(new ItemStack(Items.PAPER), null);
        };
    }

    private static Icon custom(String id) {
        return new Icon(ItemStack.EMPTY, CUSTOM.getOrDefault(id, CUSTOM.get("unknown")));
    }

    private static String inferCustom(String target, String goalId) {
        if (goalId.contains("boss_overworld")) return "boss_surface";
        if (goalId.contains("boss_nether")) return "boss_nether";
        if (goalId.contains("boss_dragon")) return "boss_dragon";
        if (goalId.contains("boss_end")) return "boss_void";
        return switch (target) {
            case "arlightbosses:surface_guardian" -> "boss_surface";
            case "arlightbosses:nether_guardian" -> "boss_nether";
            case "arlightbosses:void_guardian" -> "boss_void";
            case "arlightbosses:dragon_guardian" -> "boss_dragon";
            case "minecraft:ender_dragon" -> "boss_dragon";
            case "arlightbosses:emerald_zombie_minion" -> "emerald_zombie";
            case "arlightbosses:emerald_creeper_minion" -> "emerald_creeper";
            case "arlightbosses:gilded_piglin_minion" -> "gilded_piglin";
            case "arlightbosses:gilded_hoglin_rider_minion", "arlightbosses:gilded_hoglin_mount",
                    "arlightbosses:gilded_hoglin_minion" -> "gilded_hoglin";
            case "arlightbosses:amethyst_eye_minion" -> "amethyst_eye";
            case "arlightbosses:void_enderman_minion" -> "void_enderman";
            case "arlightbosses:amethyst_shulker_minion" -> "amethyst_shulker";
            default -> "";
        };
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
