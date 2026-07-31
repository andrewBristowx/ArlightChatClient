package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** Vista completa del cartón, sin blur ni capa oscura sobre todo el mundo. */
public final class BingoCardScreen extends Screen {
    private static final ResourceLocation PANEL = ResourceLocation.fromNamespaceAndPath(
            ArlightChatClient.MOD_ID, "textures/gui/bingo_card_screen.png");

    public BingoCardScreen() { super(Component.literal("Cartón de Bingo")); }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        List<BingoCardOverlay.GoalData> goals = BingoCardOverlay.goals();
        int size = BingoCardOverlay.size();
        int cell = Math.max(24, Math.min(42, (height - 100) / Math.max(1, size)));
        int gridW = size * cell;
        int gridH = size * cell;
        int panelW = gridW + 32;
        int panelH = gridH + 86;
        int panelX = (width - panelW) / 2;
        int panelY = Math.max(10, (height - panelH) / 2);
        int startX = panelX + 16;
        int startY = panelY + 50;

        // Solo el propio panel es opaco; el mundo queda nítido alrededor.
        BingoCardOverlay.blitScaled(g, PANEL, panelX, panelY, panelW, panelH, 320, 320);
        g.drawCenteredString(mc.font, Component.literal("★ CARTÓN DE BINGO ★"),
                width / 2, panelY + 13, 0xFFFFD86A);
        g.drawCenteredString(mc.font, Component.literal("Pasa el cursor para ver objetivo y progreso"),
                width / 2, panelY + 29, 0xFFE3D8EB);

        BingoCardOverlay.GoalData hovered = null;
        BingoGoalIconRegistry.Icon hoveredIcon = null;
        for (int i = 0; i < goals.size() && i < size * size; i++) {
            BingoCardOverlay.GoalData goal = goals.get(i);
            int col = i % size;
            int row = i / size;
            int x = startX + col * cell;
            int y = startY + row * cell;
            boolean over = mouseX >= x && mouseX < x + cell && mouseY >= y && mouseY < y + cell;
            int border = over ? 0xFFFFD86A : (goal.completed() ? 0xFF70F18B : 0xFF8A65A0);
            g.fill(x, y, x + cell - 2, y + cell - 2, border);
            g.fill(x + 2, y + 2, x + cell - 4, y + cell - 4,
                    goal.completed() ? 0xE0368E51 : 0xE0181121);

            BingoGoalIconRegistry.Icon icon = BingoCardOverlay.iconFor(goal);
            int iconSize = Math.min(24, cell - 8);
            int iconX = x + (cell - 2 - iconSize) / 2;
            int iconY = y + 5;
            BingoCardOverlay.renderIcon(g, icon, iconX, iconY, iconSize);

            if (goal.completed()) {
                g.drawString(mc.font, "✓", x + cell - 12, y + cell - 12, 0xFFB5FFC0, true);
            } else if (goal.required() > 1) {
                g.drawCenteredString(mc.font, Component.literal(goal.progress() + "/" + goal.required()),
                        x + (cell - 2) / 2, y + cell - 12, 0xFFFFFFFF);
            }
            if (over) { hovered = goal; hoveredIcon = icon; }
        }

        if (hovered != null) {
            int infoY = panelY + panelH - 28;
            String state = hovered.completed() ? "Completado" : "Progreso: " + hovered.progress() + "/" + hovered.required();
            g.drawCenteredString(mc.font, Component.literal(hovered.displayName()), width / 2,
                    infoY, hovered.completed() ? 0xFF8CFF9B : 0xFFFFE07A);
            g.drawCenteredString(mc.font, Component.literal(state), width / 2, infoY + 11, 0xFFE9E1EE);
            if (hoveredIcon != null && !hoveredIcon.isTexture() && !hoveredIcon.stack().isEmpty()) {
                g.renderTooltip(mc.font, hoveredIcon.stack(), mouseX, mouseY);
            }
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }
    @Override public boolean isPauseScreen() { return false; }
}
