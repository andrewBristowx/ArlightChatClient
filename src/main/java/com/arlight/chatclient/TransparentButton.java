package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

/** Botón transparente y silencioso para integrarse con el panel de Gestos. */
public final class TransparentButton extends AbstractButton {
    private final Runnable action;
    private final boolean iconStyle;
    private final float contentScale;

    public TransparentButton(int x, int y, int width, int height,
                             Component message, boolean iconStyle, Runnable action) {
        this(x, y, width, height, message, iconStyle, 1.0f, action);
    }

    public TransparentButton(int x, int y, int width, int height,
                             Component message, boolean iconStyle, float contentScale, Runnable action) {
        super(x, y, width, height, message);
        this.action = action;
        this.iconStyle = iconStyle;
        this.contentScale = Math.max(0.75f, contentScale);
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Intencionalmente vacío: los botones de emotes no producen sonido.
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        // Los botones solo se seleccionan con el ratón, nunca con Tab o flechas.
        return null;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isHoveredOrFocused();
        // Fondo oscuro permanente, similar al botón Gestos. Sigue siendo
        // semitransparente para no tapar completamente el mundo.
        int baseColor = iconStyle ? 0x3D080808 : 0x66080808;
        graphics.fill(getX(), getY(), getX() + width, getY() + height, baseColor);

        if (hovered) {
            int hoverColor = iconStyle ? 0x703B0D4A : 0x75301038;
            graphics.fill(getX(), getY(), getX() + width, getY() + height, hoverColor);
            graphics.fill(getX(), getY(), getX() + width, getY() + 1, 0xAAE48CFF);
            graphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, 0xAAE48CFF);
        }

        int color = hovered ? 0xFFFFFFFF : 0xFFEEDCF2;
        if (iconStyle && contentScale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().translate(getX() + width / 2.0f, getY() + height / 2.0f, 0);
            graphics.pose().scale(contentScale, contentScale, 1.0f);
            int textY = -Minecraft.getInstance().font.lineHeight / 2;
            graphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), 0, textY, color);
            graphics.pose().popPose();
        } else {
            int textY = getY() + (height - 8) / 2;
            graphics.drawCenteredString(Minecraft.getInstance().font,
                    getMessage(), getX() + width / 2, textY, color);
        }
    }
}
