package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/** Botón temático cian/rojo usado en la pantalla de cola de SkyWars. */
public final class SkyWarsButton extends AbstractButton {
    private final Runnable action;
    private final boolean danger;

    public SkyWarsButton(int x, int y, int width, int height, Component message,
                         boolean danger, Runnable action) {
        super(x, y, width, height, message);
        this.danger = danger;
        this.action = action;
    }

    @Override public void onPress() { action.run(); }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isHoveredOrFocused();
        int border = danger ? (hovered ? 0xFFFF8181 : 0xFFCF5656)
                : (hovered ? 0xFF8CF5FF : 0xFF43CDE0);
        int background = danger ? (hovered ? 0xD04A1018 : 0xC02A0B12)
                : (hovered ? 0xD0113645 : 0xC008202B);
        g.fill(getX(), getY(), getX() + width, getY() + height, border);
        g.fill(getX() + 2, getY() + 2, getX() + width - 2, getY() + height - 2, background);
        int color = hovered ? 0xFFFFFFFF : 0xFFEAFBFF;
        g.drawCenteredString(Minecraft.getInstance().font, getMessage(),
                getX() + width / 2, getY() + (height - 8) / 2, color);
    }
}
