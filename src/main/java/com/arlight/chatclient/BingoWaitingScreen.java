package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Sala persistente del Bingo; cambia a la pantalla de preparación al comenzar Chunky. */
public final class BingoWaitingScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            ArlightChatClient.MOD_ID, "textures/loading/bingo_waiting_background.png");
    private BingoWaitingButton infoButton;

    public BingoWaitingScreen() {
        super(Component.literal("Sala de espera del Bingo"));
    }

    @Override
    protected void init() {
        int panelW = Math.min(500, width - 40);
        int gap = 12;
        int buttonW = (panelW - gap) / 2;
        int buttonY = Math.min(height - 46, Math.round(height * 0.825F));
        int startX = (width - panelW) / 2;

        infoButton = addRenderableWidget(new BingoWaitingButton(startX, buttonY, buttonW, 28,
                Component.literal("¿Qué es Bingo?"), false, () -> { }));
        addRenderableWidget(new BingoWaitingButton(startX + buttonW + gap, buttonY, buttonW, 28,
                Component.literal("Salir de la cola"), true, BingoWaitingOverlay::leaveQueue));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        BingoWaitingOverlay.validateConnection();
        HudVisibilityController.keepHidden();
        Minecraft mc = Minecraft.getInstance();

        g.blit(BACKGROUND, 0, 0, 0, 0, width, height, width, height);
        g.fill(0, 0, width, height, 0x16000000);

        g.pose().pushPose();
        g.pose().scale(2.8F, 2.8F, 1.0F);
        int scaledWidth = Math.round(width / 2.8F);
        g.drawCenteredString(mc.font, Component.literal("✦ BINGO ✦"), scaledWidth / 2,
                Math.round(height * 0.12F / 2.8F), 0xFFFFD35E);
        g.pose().popPose();

        int panelW = Math.min(500, width - 40);
        int panelH = 142;
        int panelX = (width - panelW) / 2;
        int panelY = Math.min(height - panelH - 20, Math.round(height * 0.635F));

        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xD00A0A1A);
        g.fill(panelX + 2, panelY + 2, panelX + panelW - 2, panelY + 5, 0xFFFFD35E);
        g.fill(panelX + 3, panelY + 5, panelX + 6, panelY + panelH - 3, 0xFF65E8F4);
        g.fill(panelX + panelW - 6, panelY + 5, panelX + panelW - 3, panelY + panelH - 3, 0xFF65E8F4);

        g.drawCenteredString(mc.font, Component.literal("Estás dentro de la sala de espera"),
                width / 2, panelY + 15, 0xFFFFFFFF);
        g.drawCenteredString(mc.font,
                Component.literal("Jugadores: " + BingoWaitingOverlay.players() + "/"
                        + BingoWaitingOverlay.maxPlayers()),
                width / 2, panelY + 37, 0xFFFFD35E);
        g.drawCenteredString(mc.font, Component.literal("Objetivo: Completa más casillas"),
                width / 2, panelY + 57, 0xFF92F4FF);

        String helper = BingoWaitingOverlay.countdown() >= 0
                ? "La arena comenzará a prepararse en " + BingoWaitingOverlay.countdown() + "s"
                : "La partida comenzará cuando haya suficientes jugadores";
        g.drawCenteredString(mc.font, Component.literal(helper),
                width / 2, panelY + 77, 0xFFE4E0F2);

        super.render(g, mouseX, mouseY, partialTick);

        if (infoButton != null && infoButton.isHoveredOrFocused()) {
            int tipW = Math.min(450, width - 50);
            int tipH = 72;
            int tipX = (width - tipW) / 2;
            int tipY = Math.max(18, panelY - tipH - 8);
            g.fill(tipX, tipY, tipX + tipW, tipY + tipH, 0xEE090919);
            g.fill(tipX + 2, tipY + 2, tipX + tipW - 2, tipY + 5, 0xFFFFD35E);
            g.drawCenteredString(mc.font, Component.literal("Consigue objetos, rompe bloques y derrota criaturas."),
                    width / 2, tipY + 12, 0xFFFFFFFF);
            g.drawCenteredString(mc.font, Component.literal("Cada objetivo completado suma una casilla a tu cartón."),
                    width / 2, tipY + 29, 0xFFD8F8FF);
            g.drawCenteredString(mc.font, Component.literal("Gana quien complete más casillas antes de terminar el tiempo."),
                    width / 2, tipY + 46, 0xFFFFE58A);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) { }
    @Override public boolean isPauseScreen() { return false; }
}
