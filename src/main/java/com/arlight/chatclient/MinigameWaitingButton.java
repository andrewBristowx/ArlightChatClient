package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/** Botón visual compartido por TNTRun, Parkour y Build Battle. */
public final class MinigameWaitingButton extends AbstractButton {
    private final Runnable action;
    private final boolean danger;
    private final int accent;

    public MinigameWaitingButton(int x, int y, int width, int height,
                                 Component message, boolean danger, int accent,
                                 Runnable action) {
        super(x, y, width, height, message);
        this.action = action;
        this.danger = danger;
        this.accent = accent;
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isHoveredOrFocused();
        int border = danger ? (hovered ? 0xFFFF98A5 : 0xFFE86679) : accent;
        int background = danger
                ? (hovered ? 0xE0621C31 : 0xD13D1020)
                : (hovered ? 0xE12C244A : 0xD1181734);
        graphics.fill(getX(), getY(), getX() + width, getY() + height, border);
        graphics.fill(getX() + 2, getY() + 2,
                getX() + width - 2, getY() + height - 2, background);
        graphics.drawCenteredString(Minecraft.getInstance().font, getMessage(),
                getX() + width / 2, getY() + (height - 8) / 2,
                hovered ? 0xFFFFFFFF : 0xFFF7F1FF);
    }
}
