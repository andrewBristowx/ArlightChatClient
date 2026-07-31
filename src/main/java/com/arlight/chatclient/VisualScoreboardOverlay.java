package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

/** Panel visual actualizable que cubre el sidebar vanilla sin eliminar su fallback. */
public final class VisualScoreboardOverlay {
    public record Line(String icon, String label, String value) { }

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final List<Line> lines = new ArrayList<>();
    private static boolean visible;
    private static String style = "lobby";
    private static String title = "MINIJUEGOS";
    private static String footer = "";
    private static String progressText = "";
    private static double progress = -1.0D;
    /** Identidad de la conexión que envió el panel, para limpiar datos al cambiar de servidor. */
    private static Object connectionIdentity;

    private VisualScoreboardOverlay() { }

    public static void accept(String command) {
        if (command == null || command.isBlank()) return;
        String[] p = command.split("\\|", 7);
        if ("CLEAR".equals(p[0])) {
            clearInternal();
            connectionIdentity = Minecraft.getInstance().getConnection();
            return;
        }
        if (!"SHOW".equals(p[0]) || p.length < 7) return;

        style = decode(p[1]);
        title = decode(p[2]);
        footer = decode(p[3]);
        progress = parseDouble(p[4], -1.0D);
        progressText = decode(p[5]);
        lines.clear();
        if (!p[6].isBlank()) {
            for (String raw : p[6].split(";")) {
                String[] f = raw.split(",", -1);
                if (f.length >= 3) lines.add(new Line(decode(f[0]), decode(f[1]), decode(f[2])));
            }
        }
        visible = true;
        connectionIdentity = Minecraft.getInstance().getConnection();
    }

    public static void clear() {
        clearInternal();
        connectionIdentity = Minecraft.getInstance().getConnection();
    }

    private static void clearInternal() {
        visible = false;
        lines.clear();
        progress = -1.0D;
        progressText = "";
        footer = "";
    }

    /**
     * Borra el panel si la conexión cambió. Esto evita que un scoreboard de un servidor
     * anterior reaparezca al reconectar sin haber recibido todavía un paquete CLEAR.
     */
    private static boolean connectionChanged(Minecraft mc) {
        Object current = mc.getConnection();
        if (connectionIdentity != null && current != connectionIdentity) {
            clearInternal();
            connectionIdentity = current;
            return true;
        }
        if (connectionIdentity == null) connectionIdentity = current;
        return false;
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (connectionChanged(mc) || !visible || BingoPreparationOverlay.isVisible()) return;
        if (mc.options.hideGui || mc.player == null || mc.level == null) return;

        GuiGraphics g = event.getGuiGraphics();
        int panelW = 184;
        int lineH = 17;
        int lineCount = Math.max(1, lines.size());
        int progressBlock = progress >= 0.0D ? 33 : 0;
        int footerBlock = footer.isBlank() ? 0 : 18;
        int panelH = 43 + lineCount * lineH + progressBlock + footerBlock;
        int x = g.guiWidth() - panelW - 7;
        int y = 16;

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                ArlightChatClient.MOD_ID, "textures/gui/scoreboard_" + panelTextureStyle(style) + ".png");
        BingoCardOverlay.blitScaled(g, texture, x, y, panelW, panelH, 256, 192);
        g.drawCenteredString(mc.font, Component.literal("✦ " + title + " ✦"),
                x + panelW / 2, y + 11, titleColor(style));

        int lineY = y + 32;
        for (Line line : lines) {
            if (!line.icon().isBlank()) {
                ResourceLocation iconTexture = iconTexture(line.icon());
                if (iconTexture != null) {
                    blitIcon(g, iconTexture, x + 11, lineY - 2, 13);
                } else {
                    // Fallback para emotes/glifos que no tengan textura directa registrada.
                    g.drawString(mc.font, line.icon(), x + 12, lineY, 0xFFFFFFFF, true);
                }
            }
            g.drawString(mc.font, Component.literal(line.label()), x + 28, lineY, 0xFFE8E0EF, false);
            int valueX = x + panelW - 12 - mc.font.width(line.value());
            g.drawString(mc.font, Component.literal(line.value()), valueX, lineY, 0xFFFFFFFF, true);
            lineY += lineH;
        }

        if (progress >= 0.0D) {
            int barX = x + 14;
            int barY = lineY + 2;
            int barW = panelW - 28;
            int barH = 8;
            drawRoundedProgress(g, barX, barY, barW, barH,
                    Math.max(0.0D, Math.min(1.0D, progress)), accentColor(style));
            if (!progressText.isBlank()) {
                g.drawCenteredString(mc.font, Component.literal(progressText), x + panelW / 2,
                        barY + 11, 0xFFE9E1EE);
            }
        }

        if (!footer.isBlank()) {
            // El footer siempre ocupa su propia línea, debajo del texto de progreso.
            g.drawCenteredString(mc.font, Component.literal(footer), x + panelW / 2,
                    y + panelH - 19, 0xFFFFD56A);
        }
    }

    /** Barra tipo píldora con tapas realmente redondeadas, dibujada por filas de píxeles. */
    private static void drawRoundedProgress(GuiGraphics g, int x, int y, int width, int height,
                                            double value, int accent) {
        int background = 0xDD160F1D;
        fillPill(g, x, y, width, height, background);
        int innerHeight = Math.max(2, height - 2);
        int filled = (int) Math.round((width - 2) * value);
        if (filled <= 0) return;
        // Para porcentajes pequeños se conserva una tapa circular en vez de un triángulo/punta.
        fillPill(g, x + 1, y + 1, Math.max(innerHeight, filled), innerHeight, accent);
    }

    private static void fillPill(GuiGraphics g, int x, int y, int width, int height, int color) {
        if (width <= 0 || height <= 0) return;
        if (height <= 2 || width <= height / 2) {
            g.fill(x, y, x + width, y + height, color);
            return;
        }
        double radius = height / 2.0D;
        for (int row = 0; row < height; row++) {
            double dy = Math.abs((row + 0.5D) - radius);
            double curve = Math.sqrt(Math.max(0.0D, radius * radius - dy * dy));
            int inset = Math.max(0, (int) Math.ceil(radius - curve));
            int left = x + Math.min(inset, Math.max(0, width / 2));
            int right = x + width - Math.min(inset, Math.max(0, width / 2));
            if (right > left) g.fill(left, y + row, right, y + row + 1, color);
        }
    }

    private static String normalizedStyle(String value) {
        return switch (value == null ? "" : value.toLowerCase(Locale.ROOT)) {
            case "bingo" -> "bingo";
            case "skywars" -> "skywars";
            case "lucky" -> "lucky";
            case "parkour" -> "parkour";
            case "tntrun" -> "tntrun";
            case "buildbattle" -> "buildbattle";
            default -> "lobby";
        };
    }

    // Por ahora los tres minijuegos nuevos reutilizan el marco del lobby,
    // pero conservan sus propios colores y pueden recibir una textura dedicada después.
    private static String panelTextureStyle(String value) {
        return switch (normalizedStyle(value)) {
            case "bingo" -> "bingo";
            case "skywars" -> "skywars";
            case "lucky" -> "lucky";
            default -> "lobby";
        };
    }

    private static int titleColor(String value) {
        return switch (normalizedStyle(value)) {
            case "bingo" -> 0xFFFFD86A;
            case "skywars", "parkour" -> 0xFF79E7FF;
            case "lucky" -> 0xFFFFD85A;
            case "tntrun" -> 0xFFFF9A67;
            case "buildbattle" -> 0xFFFF91D1;
            default -> 0xFFF2A4FF;
        };
    }

    private static int accentColor(String value) {
        return switch (normalizedStyle(value)) {
            case "bingo" -> 0xFFFFC94A;
            case "skywars", "parkour" -> 0xFF54D7FF;
            case "lucky" -> 0xFFFFC94A;
            case "tntrun" -> 0xFFFF8058;
            case "buildbattle" -> 0xFFFF70C5;
            default -> 0xFFE777FF;
        };
    }

    /**
     * Resuelve tanto los glifos privados enviados por las versiones nuevas como
     * los nombres internos que enviaba Parkour 1.1.0. Así no se imprimen palabras
     * como timer/checkpoint/rank/fall dentro del panel.
     */
    private static ResourceLocation iconTexture(String icon) {
        if (icon == null || icon.isBlank()) return null;
        String key = switch (icon) {
            case "\uE300", "check", "checkpoint" -> "check";
            case "\uE301", "cross" -> "cross";
            case "\uE302", "warning", "fall" -> "warning";
            case "\uE303", "info" -> "info";
            case "\uE304", "trophy", "rank" -> "trophy";
            case "\uE305", "clock", "timer", "time" -> "clock";
            case "\uE306", "swords", "kills" -> "swords";
            case "\uE307", "players", "alive" -> "players";
            case "\uE308", "star" -> "star";
            default -> null;
        };
        return key == null ? null : ResourceLocation.fromNamespaceAndPath(
                ArlightChatClient.MOD_ID, "textures/gui/icons/" + key + ".png");
    }

    private static void blitIcon(GuiGraphics g, ResourceLocation texture, int x, int y, int size) {
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        float scale = size / 64.0F;
        g.pose().scale(scale, scale, 1.0F);
        g.blit(texture, 0, 0, 0, 0, 64, 64, 64, 64);
        g.pose().popPose();
    }

    private static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return "";
        try { return new String(DECODER.decode(encoded), StandardCharsets.UTF_8); }
        catch (IllegalArgumentException ignored) { return encoded; }
    }

    private static double parseDouble(String raw, double fallback) {
        try { return Double.parseDouble(raw); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
