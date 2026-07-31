package com.arlight.chatclient;

import net.minecraft.client.Minecraft;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/** Estado recibido desde ArlightCore y apertura de la pantalla universal. */
public final class UniversalPodiumOverlay {
    public record Stat(String icon, String label, String value) { }
    public record Entry(int place, UUID uuid, String name, String pose, List<Stat> stats) { }
    public record Data(String gameId, String gameName, String theme, String subtitle,
                       int displayTicks, String requeueCommand, int viewerPlace,
                       List<Entry> entries, List<Stat> viewerStats) { }

    private static Data data;

    private UniversalPodiumOverlay() { }

    public static void accept(String command) {
        Minecraft minecraft = Minecraft.getInstance();
        if (command == null || command.equals("HIDE")) {
            data = null;
            if (minecraft.screen instanceof UniversalPodiumScreen) minecraft.setScreen(null);
            HudVisibilityController.release("universal_podium");
            return;
        }
        String[] fields = command.split("\\|", -1);
        if (fields.length < 10 || !fields[0].equals("SHOW")) return;
        try {
            data = new Data(
                    decode(fields[1]), decode(fields[2]), decode(fields[3]), decode(fields[4]),
                    Math.max(80, Integer.parseInt(fields[5])), decode(fields[6]),
                    Integer.parseInt(fields[7]), parseEntries(fields[8]), parseStats(fields[9]));
            HudVisibilityController.acquire("universal_podium");
            minecraft.setScreen(new UniversalPodiumScreen(data));
        } catch (RuntimeException ignored) {
            data = null;
            HudVisibilityController.release("universal_podium");
        }
    }

    public static Data data() { return data; }

    private static List<Entry> parseEntries(String value) {
        List<Entry> entries = new ArrayList<>();
        if (value == null || value.isBlank()) return entries;
        for (String encoded : value.split(";")) {
            String[] fields = encoded.split(",", -1);
            if (fields.length < 5) continue;
            try {
                entries.add(new Entry(Integer.parseInt(fields[0]),
                        fields[1].isBlank() ? null : UUID.fromString(fields[1]),
                        decode(fields[2]), decode(fields[3]), parseStats(fields[4])));
            } catch (RuntimeException ignored) { }
        }
        return List.copyOf(entries);
    }

    private static List<Stat> parseStats(String value) {
        List<Stat> stats = new ArrayList<>();
        String decoded = decode(value);
        if (decoded.isBlank()) return stats;
        for (String line : decoded.split("\n")) {
            String[] fields = line.split("\t", -1);
            if (fields.length >= 3) stats.add(new Stat(fields[0], fields[1], fields[2]));
        }
        return List.copyOf(stats);
    }

    private static String decode(String value) {
        if (value == null || value.isEmpty()) return "";
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
