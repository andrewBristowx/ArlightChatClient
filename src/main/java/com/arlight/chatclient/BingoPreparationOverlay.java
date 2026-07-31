package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class BingoPreparationOverlay {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("arlightchat", "textures/loading/bingo_loading_background.png");

    private static final ResourceLocation[] TIP_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath("arlightchat", "textures/loading/tip_card_new.png"),
            ResourceLocation.fromNamespaceAndPath("arlightchat", "textures/loading/tip_surface_new.png"),
            ResourceLocation.fromNamespaceAndPath("arlightchat", "textures/loading/tip_nether_new.png"),
            ResourceLocation.fromNamespaceAndPath("arlightchat", "textures/loading/tip_chest_new.png"),
            ResourceLocation.fromNamespaceAndPath("arlightchat", "textures/loading/tip_end_new.png")
    };

    private static final String[] TIPS = {
            "Completa los objetivos que aparecen en tu cartón",
            "Derrota al guardián del Overworld para abrir el Nether",
            "La mazmorra del Nether desbloquea el acceso al End",
            "Los cofres pueden contener objetos útiles para tu cartón",
            "Después del dragón, conquista la torre de recompensas"
    };

    private static boolean visible;
    private static String phase = "Preparando arena";
    private static int tip;
    private static double progress;
    private static int countdown = -1;
    private static long hideAt;
    private static Object connectionIdentity;

    private BingoPreparationOverlay() {}

    public static boolean isVisible() {
        return visible;
    }

    public static void accept(String command) {
        String[] parts = command.split("\\|", -1);
        switch (parts[0]) {
            case "SHOW" -> {
                BingoWaitingOverlay.hide();
                visible = true;
                countdown = -1;
                phase = parts.length > 1 ? parts[1] : "Preparando arena";
                tip = parts.length > 2 ? parseInt(parts[2], 0) : 0;
                progress = parts.length > 3 ? parseDouble(parts[3], 0.0) : 0.0;
                hideAt = 0;
                connectionIdentity = Minecraft.getInstance().getConnection();
                HudVisibilityController.acquire("bingo_preparation");
            }
            case "COUNTDOWN" -> {
                BingoWaitingOverlay.hide();
                visible = true;
                countdown = parts.length > 1 ? parseInt(parts[1], 5) : 5;
                phase = "La partida está a punto de comenzar";
                progress = 1.0;
                connectionIdentity = Minecraft.getInstance().getConnection();
                HudVisibilityController.acquire("bingo_preparation");
            }
            case "START" -> {
                BingoWaitingOverlay.hide();
                visible = true;
                countdown = 0;
                phase = "¡COMIENZA!";
                progress = 1.0;
                hideAt = System.currentTimeMillis() + 900L;
                connectionIdentity = Minecraft.getInstance().getConnection();
                HudVisibilityController.acquire("bingo_preparation");
            }
            case "HIDE" -> {
                visible = false;
                HudVisibilityController.release("bingo_preparation");
                connectionIdentity = Minecraft.getInstance().getConnection();
            }
            default -> { }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Object current = mc.getConnection();
        if (connectionIdentity != null && current != connectionIdentity) {
            visible = false;
            HudVisibilityController.release("bingo_preparation");
            connectionIdentity = current;
            return;
        }
        if (!visible) return;
        if (hideAt > 0 && System.currentTimeMillis() >= hideAt) {
            visible = false;
            HudVisibilityController.release("bingo_preparation");
            return;
        }

        HudVisibilityController.keepHidden();
        GuiGraphics g = event.getGuiGraphics();
        int w = g.guiWidth();
        int h = g.guiHeight();

        // Fondo completamente opaco: oculta HUD, chat, scoreboard y BossBar durante la preparación.
        g.blit(BACKGROUND, 0, 0, 0, 0, w, h, w, h);

        if (countdown >= 0) {
            renderCountdown(g, mc, w, h);
            return;
        }

        // Interior real del marco del fondo 1920x1080.
        // Estas proporciones dejan intactos los bordes morados y evitan estirar
        // las ilustraciones fuera del recuadro.
        int imageX = Math.round(w * (658.0f / 1920.0f));
        int imageY = Math.round(h * (333.0f / 1080.0f));
        int imageW = Math.round(w * (604.0f / 1920.0f));
        int imageH = Math.round(h * (443.0f / 1080.0f));

        ResourceLocation texture = TIP_TEXTURES[Math.floorMod(tip, TIP_TEXTURES.length)];
        g.blit(texture, imageX, imageY, 0, 0, imageW, imageH, imageW, imageH);

        // El fondo ya no contiene texto ni barra falsa; se dibujan solo los datos reales.
        int phaseY = Math.round(h * 0.735f);
        g.drawCenteredString(mc.font, Component.literal(phase), w / 2, phaseY, 0xFFFFFFFF);
        g.drawCenteredString(mc.font,
                Component.literal(TIPS[Math.floorMod(tip, TIPS.length)]),
                w / 2, phaseY + 17, 0xFFE8E6F4);

        int barW = Math.min(Math.round(w * 0.44f), w - 50);
        int barH = Math.max(9, Math.round(h * 0.012f));
        int barX = (w - barW) / 2;
        int barY = Math.round(h * 0.815f);
        int border = Math.max(2, Math.round(h * 0.0025f));

        g.fill(barX - border, barY - border, barX + barW + border, barY + barH + border, 0xFF05050A);
        g.fill(barX, barY, barX + barW, barY + barH, 0xFF25293A);
        int filled = (int) Math.round(barW * clamp(progress));
        if (filled > 0) {
            g.fillGradient(barX, barY, barX + filled, barY + barH, 0xFF6E4FE7, 0xFFB24FFF);
        }
        g.drawCenteredString(mc.font,
                Component.literal((int) Math.round(clamp(progress) * 100.0) + "%"),
                w / 2, barY + Math.max(1, (barH - mc.font.lineHeight) / 2), 0xFFFFFFFF);
    }

    private static void renderCountdown(GuiGraphics g, Minecraft mc, int w, int h) {
        g.fill(0, 0, w, h, 0x98000000);
        String text = countdown == 0 ? "¡COMIENZA!" : Integer.toString(countdown);
        int color = countdown == 0 ? 0xFFFFD34E : 0xFFFFFFFF;

        // Escala grande mediante pose stack, centrada sin depender de la resolución.
        float scale = countdown == 0 ? 4.0f : 7.0f;
        g.pose().pushPose();
        g.pose().scale(scale, scale, 1.0f);
        int scaledW = Math.round(w / scale);
        int scaledH = Math.round(h / scale);
        g.drawCenteredString(mc.font, Component.literal(text), scaledW / 2,
                scaledH / 2 - mc.font.lineHeight / 2, color);
        g.pose().popPose();

        if (countdown > 0) {
            g.drawCenteredString(mc.font, Component.literal(phase), w / 2,
                    Math.round(h * 0.64f), 0xFFE8E6F4);
        }
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (NumberFormatException ignored) { return fallback; }
    }

    private static double parseDouble(String value, double fallback) {
        try { return Double.parseDouble(value); } catch (NumberFormatException ignored) { return fallback; }
    }
}
