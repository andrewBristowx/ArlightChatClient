package com.arlight.chatclient;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Aviso compacto para jugadores ajenos a la partida mientras Chunky prepara Bingo. */
public final class BingoWorldPreparationNoticeOverlay {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "arlightchat", "textures/gui/bingo_world_preparation_notice.png");
    private static final int TEXTURE_WIDTH = 1442;
    private static final int TEXTURE_HEIGHT = 641;

    private static boolean requestedVisible;
    private static float alpha;
    private static long lastFrameMillis;
    private static String position = "TOP_LEFT";
    private static int xOffset = 8;
    private static int yOffset = 52;
    private static double scale = 0.20;
    private static int fadeInTicks = 10;
    private static int fadeOutTicks = 10;

    private BingoWorldPreparationNoticeOverlay() {
    }

    public static void accept(String command) {
        if (command == null || command.isBlank()) return;
        String[] parts = command.split("\\|", -1);
        switch (parts[0]) {
            case "NOTICE_SHOW" -> {
                position = parts.length > 1 ? normalizePosition(parts[1]) : "TOP_LEFT";
                xOffset = parts.length > 2 ? Math.max(0, parseInt(parts[2], 8)) : 8;
                yOffset = parts.length > 3 ? Math.max(0, parseInt(parts[3], 52)) : 52;
                scale = parts.length > 4 ? clamp(parseDouble(parts[4], 0.20), 0.08, 0.30) : 0.20;
                fadeInTicks = parts.length > 5 ? clamp(parseInt(parts[5], 10), 0, 100) : 10;
                fadeOutTicks = parts.length > 6 ? clamp(parseInt(parts[6], 10), 0, 100) : 10;
                requestedVisible = true;
                lastFrameMillis = System.currentTimeMillis();
                if (fadeInTicks == 0) alpha = 1.0f;
            }
            case "NOTICE_HIDE" -> {
                requestedVisible = false;
                lastFrameMillis = System.currentTimeMillis();
                if (fadeOutTicks == 0) alpha = 0.0f;
            }
            default -> {
            }
        }
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        if (!requestedVisible && alpha <= 0.001f) return;
        if (BingoPreparationOverlay.isVisible()) return;

        long now = System.currentTimeMillis();
        if (lastFrameMillis == 0L) lastFrameMillis = now;
        long elapsed = Math.max(0L, Math.min(250L, now - lastFrameMillis));
        lastFrameMillis = now;

        float duration = Math.max(1.0f, (requestedVisible ? fadeInTicks : fadeOutTicks) * 50.0f);
        float change = elapsed / duration;
        alpha = requestedVisible ? Math.min(1.0f, alpha + change) : Math.max(0.0f, alpha - change);
        if (alpha <= 0.001f) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        // Ajuste "contain": conserva la imagen completa incluso con GUI grande o pantallas pequeñas.
        int availableWidth = Math.max(1, screenWidth - xOffset - 8);
        int availableHeight = Math.max(1, screenHeight - yOffset - 8);
        int widthByScale = Math.max(1, (int) Math.round(screenWidth * scale));
        int widthByHeight = Math.max(1, (int) Math.floor(availableHeight * (TEXTURE_WIDTH / (double) TEXTURE_HEIGHT)));
        int width = Math.min(Math.min(widthByScale, availableWidth), widthByHeight);
        int height = Math.max(1, (int) Math.round(width * (TEXTURE_HEIGHT / (double) TEXTURE_WIDTH)));

        int x = switch (position) {
            case "TOP_RIGHT", "BOTTOM_RIGHT" -> screenWidth - width - xOffset;
            default -> xOffset;
        };
        int y = switch (position) {
            case "BOTTOM_LEFT", "BOTTOM_RIGHT" -> screenHeight - height - yOffset;
            default -> yOffset;
        };

        x = Math.max(0, Math.min(x, screenWidth - width));
        y = Math.max(0, Math.min(y, screenHeight - height));

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        // Dibuja la textura COMPLETA y después la escala. El overload anterior tomaba
        // width/height también como región fuente, por eso solo mostraba una esquina ampliada.
        float renderScale = width / (float) TEXTURE_WIDTH;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0f);
        graphics.pose().scale(renderScale, renderScale, 1.0f);
        graphics.blit(TEXTURE, 0, 0, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        graphics.pose().popPose();
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static String normalizePosition(String raw) {
        if (raw == null) return "TOP_LEFT";
        return switch (raw.trim().toUpperCase()) {
            case "TOP_RIGHT" -> "TOP_RIGHT";
            case "BOTTOM_LEFT" -> "BOTTOM_LEFT";
            case "BOTTOM_RIGHT" -> "BOTTOM_RIGHT";
            default -> "TOP_LEFT";
        };
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    private static double parseDouble(String value, double fallback) {
        try { return Double.parseDouble(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
