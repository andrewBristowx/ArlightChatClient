package com.arlight.chatclient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** Catálogo autorizado enviado por ArlightChat desde el servidor. */
public final class ServerEmoteRegistry {
    private static volatile List<EmoteDefinition> emotes = EmoteRegistry.DEFAULT_EMOTES;

    private ServerEmoteRegistry() {
    }

    public static List<EmoteDefinition> getEmotes() {
        return emotes;
    }

    public static void update(String catalog) {
        List<EmoteDefinition> received = new ArrayList<>();
        for (String line : catalog.split("\\R")) {
            if (line.isBlank()) continue;
            String[] fields = line.split(",", -1);
            if (fields.length != 3 && fields.length < 5) continue;
            try {
                String alias = new String(Base64.getUrlDecoder().decode(fields[0]),
                        StandardCharsets.UTF_8);
                int codePoint = Integer.parseInt(fields[1], 16);
                String glyph = Character.toString(codePoint);
                boolean animated = Boolean.parseBoolean(fields[2]);
                String groupId = fields.length >= 5
                        ? new String(Base64.getUrlDecoder().decode(fields[3]), StandardCharsets.UTF_8)
                        : "default";
                String groupName = fields.length >= 5
                        ? new String(Base64.getUrlDecoder().decode(fields[4]), StandardCharsets.UTF_8)
                        : "Gratis";
                received.add(new EmoteDefinition(alias, glyph, animated, groupId, groupName));
            } catch (IllegalArgumentException ignored) {
                // Una entrada dañada no impide cargar el resto del catálogo.
            }
        }
        emotes = List.copyOf(received);
    }
}
