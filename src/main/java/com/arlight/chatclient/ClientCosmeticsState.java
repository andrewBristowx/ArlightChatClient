package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientCosmeticsState {
    public record Loadout(Map<CosmeticSlot, String> equipped, boolean visible) {
        public Loadout {
            EnumMap<CosmeticSlot, String> copy = new EnumMap<>(CosmeticSlot.class);
            if (equipped != null) copy.putAll(equipped);
            equipped = Map.copyOf(copy);
        }
    }

    private record PreviewKey(UUID uuid, CosmeticSlot slot) { }
    private record Preview(String cosmeticId, long expiresAt) { }

    private static final Map<UUID, Loadout> LOADOUTS = new ConcurrentHashMap<>();
    private static final Map<PreviewKey, Preview> PREVIEWS = new ConcurrentHashMap<>();
    private static volatile boolean hideOthers;
    private static volatile boolean particles = true;
    private static volatile int maxDistance = 48;

    private ClientCosmeticsState() { }

    public static void accept(String command) {
        if (command == null || command.isBlank()) return;
        String[] fields = command.split("\\|", -1);
        switch (fields[0]) {
            case "CLEAR" -> clear();
            case "FULL" -> parseFull(fields.length > 1 ? fields[1] : "");
            case "PLAYER" -> parsePlayer(fields);
            case "REMOVE" -> {
                if (fields.length > 1) {
                    try { LOADOUTS.remove(UUID.fromString(fields[1])); }
                    catch (IllegalArgumentException ignored) { }
                }
            }
            case "PREF" -> parsePreferences(fields);
            case "PREVIEW" -> parsePreview(fields);
            case "WARDROBE_OPEN" -> WardrobeState.accept("OPEN|" + (fields.length > 1 ? fields[1] : "") + "|" + (fields.length > 2 ? fields[2] : ""));
            default -> { }
        }
    }

    public static void clear() {
        LOADOUTS.clear();
        PREVIEWS.clear();
        hideOthers = false;
        particles = true;
        maxDistance = 48;
    }

    public static Loadout get(UUID uuid) {
        return uuid == null ? null : LOADOUTS.get(uuid);
    }

    public static String cosmetic(UUID uuid, CosmeticSlot slot) {
        if (uuid == null || slot == null) return null;
        Preview preview = PREVIEWS.get(new PreviewKey(uuid, slot));
        long now = gameTime();
        if (preview != null) {
            if (preview.expiresAt() >= now) return preview.cosmeticId();
            PREVIEWS.remove(new PreviewKey(uuid, slot));
        }
        Loadout loadout = LOADOUTS.get(uuid);
        return loadout == null ? null : loadout.equipped().get(slot);
    }

    public static boolean shouldRender(Player target) {
        Minecraft minecraft = Minecraft.getInstance();
        if (target == null || minecraft.player == null || minecraft.level == null) return false;
        Loadout loadout = LOADOUTS.get(target.getUUID());
        boolean hasPreview = PREVIEWS.keySet().stream().anyMatch(key -> key.uuid().equals(target.getUUID()));
        if ((loadout == null || !loadout.visible() || loadout.equipped().isEmpty()) && !hasPreview) return false;
        if (target.isSpectator() || target.isInvisible()) return false;
        if (hideOthers && target != minecraft.player) return false;
        return minecraft.player.distanceToSqr(target) <= (double) maxDistance * maxDistance;
    }

    public static boolean particlesEnabled() {
        return particles;
    }

    public static int maxDistance() {
        return maxDistance;
    }

    public static void tick() {
        long now = gameTime();
        PREVIEWS.entrySet().removeIf(entry -> entry.getValue().expiresAt() < now);
    }

    private static void parseFull(String encodedRecords) {
        LOADOUTS.clear();
        if (encodedRecords == null || encodedRecords.isBlank()) return;
        for (String record : encodedRecords.split(";")) {
            String[] fields = record.split(",", -1);
            if (fields.length < 3) continue;
            try {
                UUID uuid = UUID.fromString(fields[0]);
                LOADOUTS.put(uuid, new Loadout(parseLoadout(fields[1]), fields[2].equals("1")));
            } catch (IllegalArgumentException ignored) { }
        }
    }

    private static void parsePlayer(String[] fields) {
        if (fields.length < 4) return;
        try {
            UUID uuid = UUID.fromString(fields[1]);
            LOADOUTS.put(uuid, new Loadout(parseLoadout(fields[2]), fields[3].equals("1")));
        } catch (IllegalArgumentException ignored) { }
    }

    private static void parsePreferences(String[] fields) {
        if (fields.length > 1) hideOthers = fields[1].equals("1");
        if (fields.length > 2) particles = fields[2].equals("1");
        if (fields.length > 3) {
            try { maxDistance = Math.max(8, Integer.parseInt(fields[3])); }
            catch (NumberFormatException ignored) { }
        }
    }

    private static void parsePreview(String[] fields) {
        if (fields.length < 5) return;
        try {
            UUID uuid = UUID.fromString(fields[1]);
            CosmeticSlot slot = CosmeticSlot.parse(fields[2]);
            int ticks = Math.max(20, Integer.parseInt(fields[4]));
            if (slot != null && !fields[3].isBlank()) {
                PREVIEWS.put(new PreviewKey(uuid, slot), new Preview(fields[3], gameTime() + ticks));
            }
        } catch (IllegalArgumentException ignored) { }
    }

    private static Map<CosmeticSlot, String> parseLoadout(String encoded) {
        EnumMap<CosmeticSlot, String> result = new EnumMap<>(CosmeticSlot.class);
        String decoded = decode(encoded);
        if (decoded.isBlank()) return result;
        for (String line : decoded.split("\\n")) {
            int separator = line.indexOf('=');
            if (separator <= 0 || separator >= line.length() - 1) continue;
            CosmeticSlot slot = CosmeticSlot.parse(line.substring(0, separator));
            if (slot != null) result.put(slot, line.substring(separator + 1));
        }
        return result;
    }

    private static String decode(String encoded) {
        if (encoded == null || encoded.isBlank()) return "";
        try {
            return new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            return "";
        }
    }

    private static long gameTime() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? 0L : minecraft.level.getGameTime();
    }
}
