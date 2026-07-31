package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Pantalla visual de cola para TNTRun, Parkour y Build Battle. */
public final class MinigameWaitingScreen extends Screen {
    private final MinigameWaitingOverlay.Game game;
    private boolean helpVisible;

    public MinigameWaitingScreen(MinigameWaitingOverlay.Game game) {
        super(Component.literal("Sala de espera de " + game.title()));
        this.game = game;
    }

    public MinigameWaitingOverlay.Game game() {
        return game;
    }

    @Override
    protected void init() {
        int cardX = Math.max(18, Math.round(width * 0.045F));
        int cardW = Math.min(390, Math.max(280, Math.round(width * 0.43F)));
        int buttonGap = 10;
        int buttonW = (cardW - 28 - buttonGap) / 2;
        int buttonY = Math.min(height - 45, Math.round(height * 0.82F));

        addRenderableWidget(new MinigameWaitingButton(
                cardX + 14, buttonY, buttonW, 28,
                Component.literal("¿Cómo jugar?"), false, game.accent(),
                () -> helpVisible = !helpVisible));
        addRenderableWidget(new MinigameWaitingButton(
                cardX + 14 + buttonW + buttonGap, buttonY, buttonW, 28,
                Component.literal("Salir"), true, game.accent(),
                MinigameWaitingOverlay::leaveQueue));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        MinigameWaitingOverlay.validateConnection();
        HudVisibilityController.keepHidden();
        Minecraft minecraft = Minecraft.getInstance();

        // Los fondos aprobados tienen la misma relación y resolución que los fondos
        // de Bingo/SkyWars existentes (1672x941), por lo que se escalan a pantalla.
        graphics.blit(game.background(), 0, 0, 0, 0, width, height, width, height);
        graphics.fill(0, 0, width, height, 0x12000000);

        int cardX = Math.max(18, Math.round(width * 0.045F));
        int cardY = Math.max(18, Math.round(height * 0.075F));
        int cardW = Math.min(390, Math.max(280, Math.round(width * 0.43F)));
        int cardBottom = Math.min(height - 18, Math.round(height * 0.88F));
        int cardH = cardBottom - cardY;

        graphics.fill(cardX, cardY, cardX + cardW, cardY + cardH, 0xD20A0B1B);
        graphics.fill(cardX + 2, cardY + 2, cardX + cardW - 2, cardY + 6, game.accent());
        graphics.fill(cardX + 2, cardY + 6, cardX + 5, cardY + cardH - 2,
                game.accentSecondary());

        graphics.pose().pushPose();
        graphics.pose().translate(cardX + 18, cardY + 20, 0);
        graphics.pose().scale(2.05F, 2.05F, 1.0F);
        graphics.drawString(minecraft.font, Component.literal(game.title()), 0, 0,
                game.accent(), false);
        graphics.pose().popPose();

        int contentX = cardX + 18;
        int contentW = cardW - 36;
        int y = cardY + 54;
        drawWrappedCentered(graphics, minecraft, game.summary(), contentX, y, contentW, 0xFFF4EEFA);
        y += 35;

        String status = statusText();
        int badgeW = Math.min(contentW, minecraft.font.width(status) + 20);
        graphics.fill(contentX, y, contentX + badgeW, y + 18, 0xA92A2344);
        graphics.fill(contentX, y, contentX + 3, y + 18, game.accentSecondary());
        graphics.drawString(minecraft.font, Component.literal(status), contentX + 9, y + 5,
                0xFFFFFFFF, false);
        y += 28;

        if (helpVisible) {
            graphics.drawString(minecraft.font, Component.literal("CÓMO JUGAR"), contentX, y,
                    game.accentSecondary(), false);
            y += 18;
            int number = 1;
            for (String line : game.help()) {
                graphics.drawString(minecraft.font,
                        Component.literal(number + ". " + line), contentX, y,
                        0xFFE9E1F2, false);
                y += 18;
                number++;
            }
            graphics.drawString(minecraft.font,
                    Component.literal("Pulsa el botón otra vez para volver a la cola."),
                    contentX, y + 8, 0xFFC8BED4, false);
        } else {
            graphics.drawString(minecraft.font,
                    Component.literal("JUGADORES EN COLA  "
                            + MinigameWaitingOverlay.players() + "/"
                            + MinigameWaitingOverlay.maxPlayers()),
                    contentX, y, game.accentSecondary(), false);
            y += 18;

            List<String> names = MinigameWaitingOverlay.playerNames();
            int visibleNames = Math.min(8, names.size());
            for (int index = 0; index < visibleNames; index++) {
                String name = names.get(index);
                int rowY = y + index * 17;
                graphics.fill(contentX, rowY, contentX + contentW, rowY + 14, 0x64141429);
                graphics.fill(contentX + 5, rowY + 4, contentX + 11, rowY + 10,
                        game.accent());
                graphics.drawString(minecraft.font, Component.literal(name),
                        contentX + 18, rowY + 3, 0xFFF8F4FF, false);
            }
            if (names.isEmpty()) {
                graphics.drawString(minecraft.font, Component.literal("Esperando jugadores..."),
                        contentX, y + 3, 0xFFC8BED4, false);
            } else if (names.size() > visibleNames) {
                graphics.drawString(minecraft.font,
                        Component.literal("+" + (names.size() - visibleNames) + " jugadores más"),
                        contentX, y + visibleNames * 17 + 2, 0xFFC8BED4, false);
            }
        }

        String footer = MinigameWaitingOverlay.countdown() >= 0
                ? "La partida comienza en " + MinigameWaitingOverlay.countdown() + " segundos"
                : "La partida comenzará cuando haya suficientes jugadores";
        int footerY = Math.min(cardY + cardH - 75, Math.round(height * 0.75F));
        graphics.fill(contentX, footerY, contentX + contentW, footerY + 26, 0x8A111229);
        graphics.drawCenteredString(minecraft.font, Component.literal(footer),
                contentX + contentW / 2, footerY + 9, 0xFFFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private String statusText() {
        return switch (MinigameWaitingOverlay.status()) {
            case "CUENTA_REGRESIVA" -> "CUENTA REGRESIVA";
            case "GENERANDO_MAPA" -> "GENERANDO MAPA";
            case "PREPARANDO_PARTIDA" -> "PREPARANDO PARTIDA";
            default -> "ESPERANDO JUGADORES";
        };
    }

    private void drawWrappedCentered(GuiGraphics graphics, Minecraft minecraft, String text,
                                     int x, int y, int width, int color) {
        List<net.minecraft.util.FormattedCharSequence> lines = minecraft.font.split(
                Component.literal(text), width);
        int lineY = y;
        for (net.minecraft.util.FormattedCharSequence line : lines) {
            graphics.drawString(minecraft.font, line, x, lineY, color, false);
            lineY += 11;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.keyChat.matches(keyCode, scanCode)) {
            minecraft.setScreen(new ChatScreen(""));
            return true;
        }
        if (minecraft.options.keyCommand.matches(keyCode, scanCode)) {
            minecraft.setScreen(new ChatScreen("/"));
            return true;
        }
        // ESC no permite abandonar accidentalmente la pantalla/cola.
        if (keyCode == 256) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
