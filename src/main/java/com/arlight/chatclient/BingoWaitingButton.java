package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/** Botón azul/dorado o rojo para la pantalla de cola del Bingo. */
public final class BingoWaitingButton extends AbstractButton {
    private final Runnable action;
    private final boolean danger;

    public BingoWaitingButton(int x, int y, int width, int height, Component message,
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
        int border = danger ? (hovered ? 0xFFFF8787 : 0xFFE05A64)
                : (hovered ? 0xFF9AF6FF : 0xFF58DAF0);
        int innerBorder = danger ? 0xFFFFC45C : 0xFFFFD36A;
        int background = danger ? (hovered ? 0xE0581220 : 0xD0380D18)
                : (hovered ? 0xE0154A64 : 0xD00B2C42);
        g.fill(getX(), getY(), getX() + width, getY() + height, border);
        g.fill(getX() + 2, getY() + 2, getX() + width - 2, getY() + height - 2, innerBorder);
        g.fill(getX() + 3, getY() + 3, getX() + width - 3, getY() + height - 3, background);
        g.drawCenteredString(Minecraft.getInstance().font, getMessage(),
                getX() + width / 2, getY() + (height - 8) / 2,
                hovered ? 0xFFFFFFFF : 0xFFF4F0FF);
    }
}
