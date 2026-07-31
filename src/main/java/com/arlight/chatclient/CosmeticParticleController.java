package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class CosmeticParticleController {
    private static int ticks;

    private CosmeticParticleController() { }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientCosmeticsState.tick();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            ClientCosmeticsState.clear();
            ticks = 0;
            return;
        }
        ticks++;
        if (!ClientCosmeticsState.particlesEnabled() || ticks % 2 != 0) return;

        ClientLevel level = minecraft.level;
        for (Player player : level.players()) {
            if (!ClientCosmeticsState.shouldRender(player)) continue;
            String aura = ClientCosmeticsState.cosmetic(player.getUUID(), CosmeticSlot.AURA);
            if (aura != null) spawnAura(level, player, aura);
            String trail = ClientCosmeticsState.cosmetic(player.getUUID(), CosmeticSlot.TRAIL);
            if (trail != null && player.getDeltaMovement().horizontalDistanceSqr() > 0.0025D) {
                spawnTrail(level, player, trail);
            }
        }
    }

    private static void spawnAura(ClientLevel level, Player player, String id) {
        double time = (level.getGameTime() + player.getId() * 7L) * 0.16D;
        double radius = 0.72D;
        double x = player.getX() + Math.cos(time) * radius;
        double y = player.getY() + 1.05D + Math.sin(time * 0.7D) * 0.24D;
        double z = player.getZ() + Math.sin(time) * radius;
        switch (id) {
            case "bingo_emerald_orbit" -> {
                particle(level, ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0D, 0.015D, 0.0D);
                if (ticks % 6 == 0) particle(level, ParticleTypes.END_ROD, x, y, z, 0.0D, 0.01D, 0.0D);
            }
            case "bingo_gold_orbit" -> {
                particle(level, ParticleTypes.FIREWORK, x, y, z, 0.0D, 0.01D, 0.0D);
                if (ticks % 6 == 0) particle(level, ParticleTypes.CRIT, x, y, z, 0.0D, 0.01D, 0.0D);
            }
            case "bingo_amethyst_orbit" -> {
                particle(level, ParticleTypes.WITCH, x, y, z, 0.0D, 0.01D, 0.0D);
                if (ticks % 6 == 0) particle(level, ParticleTypes.ENCHANT, x, y, z, 0.0D, 0.01D, 0.0D);
            }
            case "pony_whale_stars" -> {
                particle(level, ParticleTypes.END_ROD, x, y, z, 0.0D, 0.012D, 0.0D);
                particle(level, ParticleTypes.ENCHANT,
                        player.getX() - Math.cos(time) * radius,
                        player.getY() + 1.38D,
                        player.getZ() - Math.sin(time) * radius,
                        0.0D, 0.01D, 0.0D);
            }
            case "somita_crimson_hearts" -> {
                particle(level, ParticleTypes.WITCH, x, y, z, 0.0D, 0.008D, 0.0D);
                if (ticks % 4 == 0) particle(level, ParticleTypes.PORTAL,
                        player.getX() - Math.cos(time) * radius,
                        player.getY() + 1.25D,
                        player.getZ() - Math.sin(time) * radius,
                        0.0D, 0.0D, 0.0D);
            }
            default -> { }
        }
    }

    private static void spawnTrail(ClientLevel level, Player player, String id) {
        double side = Mth.sin((level.getGameTime() + player.getId()) * 0.7F) * 0.18D;
        double x = player.getX() + side;
        double y = player.getY() + 0.08D;
        double z = player.getZ() - side;
        switch (id) {
            case "pony_pastel_steps" -> {
                particle(level, ticks % 4 == 0 ? ParticleTypes.END_ROD : ParticleTypes.ENCHANT,
                        x, y, z, 0.0D, 0.012D, 0.0D);
                if (ticks % 6 == 0) particle(level, ParticleTypes.HAPPY_VILLAGER,
                        x - side, y, z + side, 0.0D, 0.01D, 0.0D);
            }
            case "somita_night_steps" -> {
                particle(level, ParticleTypes.WITCH, x, y, z, 0.0D, 0.006D, 0.0D);
                if (ticks % 6 == 0) particle(level, ParticleTypes.CRIT,
                        x - side, y, z + side, 0.0D, 0.005D, 0.0D);
            }
            case "bingo_corruption_steps" -> {
                ParticleOptions option = switch ((ticks / 2) % 3) {
                    case 0 -> ParticleTypes.HAPPY_VILLAGER;
                    case 1 -> ParticleTypes.FIREWORK;
                    default -> ParticleTypes.WITCH;
                };
                particle(level, option, x, y, z, 0.0D, 0.008D, 0.0D);
            }
            default -> { }
        }
    }

    private static void particle(ClientLevel level, ParticleOptions type,
                                 double x, double y, double z,
                                 double dx, double dy, double dz) {
        level.addParticle(type, x, y, z, dx, dy, dz);
    }
}
