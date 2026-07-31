package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Pantalla persistente de la cola de SkyWars con información y salida. */
public final class SkyWarsWaitingScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            ArlightChatClient.MOD_ID, "textures/loading/skywars_waiting_background.png");
    private SkyWarsButton infoButton;

    public SkyWarsWaitingScreen() {
        super(Component.literal("Sala de espera de SkyWars"));
    }

    @Override
    protected void init() {
        int panelW = Math.min(440, width - 40);
        int gap = 10;
        int buttonW = (panelW - gap) / 2;
        int buttonY = Math.min(height - 44, Math.round(height * 0.82F));
        int startX = (width - panelW) / 2;

        infoButton = addRenderableWidget(new SkyWarsButton(startX, buttonY, buttonW, 26,
                Component.literal("¿Qué es SkyWars?"), false, () -> { }));
        addRenderableWidget(new SkyWarsButton(startX + buttonW + gap, buttonY, buttonW, 26,
                Component.literal("Salir de la cola"), true, SkyWarsWaitingOverlay::leaveQueue));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SkyWarsWaitingOverlay.validateConnection();
        HudVisibilityController.keepHidden();
        Minecraft mc = Minecraft.getInstance();

        g.blit(BACKGROUND, 0, 0, 0, 0, width, height, width, height);
        g.fill(0, 0, width, height, 0x18000000);

        g.pose().pushPose();
        g.pose().scale(2.5F, 2.5F, 1.0F);
        int scaledWidth = Math.round(width / 2.5F);
        g.drawCenteredString(mc.font, Component.literal("✦ SKYWARS ✦"), scaledWidth / 2,
                Math.round(height * 0.13F / 2.5F), 0xFF72E9FF);
        g.pose().popPose();

        int panelW = Math.min(440, width - 40);
        int panelH = 124;
        int panelX = (width - panelW) / 2;
        int panelY = Math.min(height - panelH - 22, Math.round(height * 0.66F));
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xD00A1320);
        g.fill(panelX + 2, panelY + 2, panelX + panelW - 2, panelY + 5, 0xFF58DFFF);
        g.drawCenteredString(mc.font, Component.literal("Estás dentro de la sala de espera"),
                width / 2, panelY + 14, 0xFFFFFFFF);
        g.drawCenteredString(mc.font,
                Component.literal("Jugadores: " + SkyWarsWaitingOverlay.players() + "/"
                        + SkyWarsWaitingOverlay.maxPlayers()),
                width / 2, panelY + 34, 0xFFFFE58A);
        g.drawCenteredString(mc.font,
                Component.literal("Mapa: " + SkyWarsWaitingOverlay.map()),
                width / 2, panelY + 50, 0xFFB9F4FF);
        g.drawCenteredString(mc.font, Component.literal("La partida comenzará cuando haya suficientes jugadores"),
                width / 2, panelY + 68, 0xFFD7E8ED);

        super.render(g, mouseX, mouseY, partialTick);

        if (infoButton != null && infoButton.isHoveredOrFocused()) {
            int tipW = Math.min(400, width - 50);
            int tipH = 62;
            int tipX = (width - tipW) / 2;
            int tipY = Math.max(18, panelY - tipH - 8);
            g.fill(tipX, tipY, tipX + tipW, tipY + tipH, 0xEE07141D);
            g.fill(tipX + 2, tipY + 2, tipX + tipW - 2, tipY + 4, 0xFF58DFFF);
            g.drawCenteredString(mc.font, Component.literal("SkyWars es un combate en islas flotantes."),
                    width / 2, tipY + 10, 0xFFFFFFFF);
            g.drawCenteredString(mc.font, Component.literal("Busca equipo en los cofres y construye puentes."),
                    width / 2, tipY + 24, 0xFFD6F7FF);
            g.drawCenteredString(mc.font, Component.literal("El último jugador con vida gana la partida."),
                    width / 2, tipY + 38, 0xFFFFE58A);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC no cierra la pantalla: se sale únicamente con el botón de la cola.
        if (keyCode == 256) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) { }
    @Override public boolean isPauseScreen() { return false; }
}
