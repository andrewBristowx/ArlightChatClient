package com.arlight.chatclient;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Renderiza emotes en el podio sin depender de que el resource pack haya añadido
 * sus glifos a minecraft:default. También convierte alias como :somi: dentro de
 * nombres, subtítulos y estadísticas de todos los minijuegos universales.
 */
public final class PodiumEmoteRenderer {
    private record Sprite(String alias, String glyph, String file, int sourceWidth, int sourceHeight) {
        ResourceLocation texture() {
            return ResourceLocation.fromNamespaceAndPath(
                    ArlightChatClient.MOD_ID, "textures/emotes/" + file + ".png");
        }
    }
    private sealed interface Token permits TextToken, SpriteToken { }
    private record TextToken(String text) implements Token { }
    private record SpriteToken(Sprite sprite) implements Token { }

    private static final List<Sprite> SPRITES = List.of(
            new Sprite(":somi:", "\uE101", "somi", 64, 64),
            new Sprite(":ponicalva:", "\uE102", "ponicalva", 64, 64),
            new Sprite(":ponino:", "\uE103", "ponino", 64, 64),
            new Sprite(":ponisi:", "\uE104", "ponisi", 64, 64),
            new Sprite(":ponisix:", "\uE105", "ponisix", 64, 65),
            new Sprite(":poniuvu:", "\uE106", "poniuvu", 64, 64),
            new Sprite(":somiseven:", "\uE107", "somiseven", 60, 63),
            new Sprite(":angy:", "\uE108", "pony0nangy", 64, 64),
            new Sprite(":love:", "\uE109", "pony0nlove", 64, 64),
            new Sprite(":jeje:", "\uE10A", "pony0njeje", 64, 64),
            new Sprite(":mad:", "\uE10B", "pony0nmad", 64, 64),
            new Sprite(":amen:", "\uE10C", "pony0namen", 64, 64),
            new Sprite(":pony0na:", "\uE10D", "pony0na", 64, 64)
    );
    private static final Map<String, Sprite> BY_MARKER = createIndex();
    private static final Map<String, String> STAT_ICONS = Map.ofEntries(
            Map.entry("trophy", "trophy"), Map.entry("rank", "trophy"), Map.entry("\uE304", "trophy"),
            Map.entry("check", "check"), Map.entry("checkpoint", "check"), Map.entry("checkpoints", "check"), Map.entry("\uE305", "check"),
            Map.entry("warning", "warning"), Map.entry("falls", "warning"), Map.entry("deaths", "warning"), Map.entry("\uE306", "warning"),
            Map.entry("clock", "clock"), Map.entry("time", "clock"), Map.entry("\uE307", "clock"),
            Map.entry("star", "star"), Map.entry("score", "star"), Map.entry("points", "star"), Map.entry("\uE308", "star"),
            Map.entry("swords", "swords"), Map.entry("kills", "swords"), Map.entry("\uE309", "swords"),
            Map.entry("players", "players"), Map.entry("player", "players"), Map.entry("\uE30A", "players"),
            Map.entry("info", "info"), Map.entry("\uE30B", "info")
    );

    private PodiumEmoteRenderer() { }

    private static Map<String, Sprite> createIndex() {
        Map<String, Sprite> result = new LinkedHashMap<>();
        for (Sprite sprite : SPRITES) {
            result.put(sprite.alias(), sprite);
            result.put(sprite.glyph(), sprite);
        }
        return Map.copyOf(result);
    }

    public static void drawCentered(GuiGraphics graphics, Font font, String text,
                                    int centerX, int y, int color, boolean shadow) {
        int width = width(font, text, 14);
        draw(graphics, font, text, centerX - width / 2, y, color, shadow, 14);
    }

    public static void draw(GuiGraphics graphics, Font font, String text,
                            int x, int y, int color, boolean shadow) {
        draw(graphics, font, text, x, y, color, shadow, 14);
    }

    private static void draw(GuiGraphics graphics, Font font, String text,
                             int x, int y, int color, boolean shadow, int iconSize) {
        int cursor = x;
        for (Token token : tokenize(text)) {
            if (token instanceof TextToken plain) {
                graphics.drawString(font, plain.text(), cursor, y, color, shadow);
                cursor += font.width(plain.text());
            } else if (token instanceof SpriteToken icon) {
                Sprite sprite = icon.sprite();
                // El overload de blit usa width/height también como tamaño del
                // recorte UV. Pasar aquí 64/128 como atlas recortaba únicamente
                // la esquina superior izquierda del emote. Un atlas lógico del
                // tamaño de destino mapea la textura completa al cuadro.
                graphics.blit(sprite.texture(), cursor, y - 3, 0, 0,
                        iconSize, iconSize, iconSize, iconSize);
                cursor += iconSize + 1;
            }
        }
    }

    public static int width(Font font, String text, int iconSize) {
        int result = 0;
        for (Token token : tokenize(text)) {
            result += token instanceof TextToken plain
                    ? font.width(plain.text()) : iconSize + 1;
        }
        return result;
    }

    /** Insignia fija de posición: nunca reutiliza emotes personales como medallas. */
    public static void drawEntryBadge(GuiGraphics graphics, String ignoredPose, int place,
                                      int centerX, int y, int size) {
        int safePlace = Math.max(1, Math.min(3, place));
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                ArlightChatClient.MOD_ID, "textures/gui/podium/place_" + safePlace + ".png");
        int padding = 2;
        int left = centerX - size / 2;
        // Fondo mínimo: la ilustración conserva su transparencia y deja de verse
        // como un emote gigante pegado encima del jugador.
        graphics.fill(left - padding, y - padding, left + size + padding, y + size + padding, 0x42090A18);
        graphics.blit(texture, left, y, 0, 0, size, size, size, size);
    }

    /** Dibuja el icono estructurado de una estadística y evita mostrar su id como texto. */
    public static int drawStatIcon(GuiGraphics graphics, String rawIcon, int x, int y, int size) {
        String icon = normalizeStatIcon(rawIcon);
        if (icon == null) return 0;
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                ArlightChatClient.MOD_ID, "textures/gui/icons/" + icon + ".png");
        graphics.blit(texture, x, y, 0, 0, size, size, size, size);
        return size + 4;
    }

    public static String normalizeStatIcon(String rawIcon) {
        if (rawIcon == null || rawIcon.isBlank()) return null;
        String normalized = rawIcon.strip().toLowerCase(java.util.Locale.ROOT);
        if (normalized.startsWith(":") && normalized.endsWith(":") && normalized.length() > 2) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        return STAT_ICONS.get(normalized);
    }

    private static Sprite findFirst(String text) {
        if (text == null || text.isBlank()) return null;
        for (Sprite sprite : SPRITES) {
            if (text.contains(sprite.alias()) || text.contains(sprite.glyph())) return sprite;
        }
        return null;
    }

    private static List<Token> tokenize(String value) {
        String text = value == null ? "" : value;
        List<Token> result = new ArrayList<>();
        int index = 0;
        while (index < text.length()) {
            int bestIndex = -1;
            String bestMarker = null;
            Sprite bestSprite = null;
            for (Map.Entry<String, Sprite> entry : BY_MARKER.entrySet()) {
                int found = text.indexOf(entry.getKey(), index);
                if (found >= 0 && (bestIndex < 0 || found < bestIndex
                        || (found == bestIndex && entry.getKey().length() > bestMarker.length()))) {
                    bestIndex = found;
                    bestMarker = entry.getKey();
                    bestSprite = entry.getValue();
                }
            }
            if (bestIndex < 0) {
                if (index < text.length()) result.add(new TextToken(text.substring(index)));
                break;
            }
            if (bestIndex > index) result.add(new TextToken(text.substring(index, bestIndex)));
            result.add(new SpriteToken(bestSprite));
            index = bestIndex + bestMarker.length();
        }
        if (result.isEmpty()) result.add(new TextToken(text));
        return result;
    }
}
