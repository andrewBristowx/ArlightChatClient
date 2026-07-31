package com.arlight.chatclient;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public final class PanelBackgroundWidget extends AbstractWidget {
    public PanelBackgroundWidget(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
        this.active = false;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xD0101010);
        graphics.fill(getX(), getY(), getX() + width, getY() + 20, 0xE0260D30);
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font,
                getMessage(), getX() + width / 2, getY() + 6, 0xFFF2A4FF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
