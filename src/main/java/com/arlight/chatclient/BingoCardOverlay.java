package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.nio.charset.StandardCharsets;
import java.util.*;

/** Cartón lateral sincronizado por ArlightBingo. */
public final class BingoCardOverlay {
    public record GoalData(String id, String type, String target, String displayName,
                           String iconKey, int progress, int required, boolean completed) { }

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final List<GoalData> goals = new ArrayList<>();
    private static final ResourceLocation PANEL = ResourceLocation.fromNamespaceAndPath(
            ArlightChatClient.MOD_ID, "textures/gui/bingo_card_sidebar.png");
    private static int size = 5;
    private static boolean visible;
    private static Object connectionIdentity;

    private BingoCardOverlay() { }

    public static void accept(String command) {
        if (command == null || command.isBlank()) return;
        String[] parts = command.split("\\|", 3);
        switch (parts[0]) {
            case "SET2" -> {
                parseV2(parts);
                connectionIdentity = Minecraft.getInstance().getConnection();
            }
            case "SET" -> {
                parseLegacy(parts);
                connectionIdentity = Minecraft.getInstance().getConnection();
            }
            case "OPEN" -> openScreen();
            case "TOGGLE" -> visible = !visible;
            case "HIDE" -> visible = false;
            case "CLEAR" -> {
                clearInternal();
                connectionIdentity = Minecraft.getInstance().getConnection();
            }
            default -> { }
        }
    }

    public static void clear() {
        clearInternal();
        connectionIdentity = Minecraft.getInstance().getConnection();
    }

    private static void clearInternal() {
        visible = false;
        goals.clear();
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof BingoCardScreen) mc.setScreen(null);
    }

    private static boolean connectionChanged(Minecraft mc) {
        Object current = mc.getConnection();
        if (connectionIdentity != null && current != connectionIdentity) {
            clearInternal();
            connectionIdentity = current;
            return true;
        }
        if (connectionIdentity == null) connectionIdentity = current;
        return false;
    }

    private static void parseV2(String[] parts) {
        size = parts.length > 1 ? Math.max(1, parseInt(parts[1], 5)) : 5;
        goals.clear();
        if (parts.length > 2 && !parts[2].isBlank()) {
            for (String rawGoal : parts[2].split(";")) {
                String[] f = rawGoal.split(",", -1);
                if (f.length < 8) continue;
                goals.add(new GoalData(decode(f[0]), f[1], decode(f[2]), decode(f[3]),
                        decode(f[4]), parseInt(f[5], 0), Math.max(1, parseInt(f[6], 1)),
                        "1".equals(f[7])));
            }
        }
        visible = !goals.isEmpty();
    }

    private static void parseLegacy(String[] parts) {
        size = parts.length > 1 ? Math.max(1, parseInt(parts[1], 5)) : 5;
        goals.clear();
        if (parts.length > 2 && !parts[2].isBlank()) {
            int index = 0;
            for (String rawGoal : parts[2].split(";")) {
                String[] f = rawGoal.split(",", -1);
                if (f.length < 6) continue;
                goals.add(new GoalData("legacy_" + index++, f[0], decode(f[1]), decode(f[2]), "",
                        parseInt(f[3], 0), Math.max(1, parseInt(f[4], 1)), "1".equals(f[5])));
            }
        }
        visible = !goals.isEmpty();
    }

    public static void openScreen() {
        if (!goals.isEmpty()) Minecraft.getInstance().setScreen(new BingoCardScreen());
    }
    public static int size() { return size; }
    public static List<GoalData> goals() { return Collections.unmodifiableList(goals); }
    public static BingoGoalIconRegistry.Icon iconFor(GoalData goal) { return BingoGoalIconRegistry.resolve(goal); }

    public static void renderIcon(GuiGraphics g, BingoGoalIconRegistry.Icon icon,
                                  int x, int y, int drawSize) {
        if (icon == null) return;
        if (icon.isTexture()) {
            float scale = drawSize / 32.0f;
            g.pose().pushPose();
            g.pose().translate(x, y, 0);
            g.pose().scale(scale, scale, 1.0f);
            g.blit(icon.texture(), 0, 0, 0, 0, 32, 32, 32, 32);
            g.pose().popPose();
        } else if (icon.stack() != null && !icon.stack().isEmpty()) {
            if (drawSize == 16) g.renderItem(icon.stack(), x, y);
            else {
                float scale = drawSize / 16.0f;
                g.pose().pushPose();
                g.pose().translate(x, y, 0);
                g.pose().scale(scale, scale, 1.0f);
                g.renderItem(icon.stack(), 0, 0);
                g.pose().popPose();
            }
        }
    }


    public static void blitScaled(GuiGraphics g, ResourceLocation texture,
                                  int x, int y, int width, int height,
                                  int textureWidth, int textureHeight) {
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(width / (float) textureWidth, height / (float) textureHeight, 1.0f);
        g.blit(texture, 0, 0, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        g.pose().popPose();
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (connectionChanged(mc) || !visible || goals.isEmpty() || BingoPreparationOverlay.isVisible()
                || mc.screen instanceof BingoCardScreen || mc.player == null || mc.level == null) return;
        GuiGraphics g = event.getGuiGraphics();
        int cell = 19;
        int grid = size * cell;
        int panelW = grid + 16;
        int panelH = grid + 34;
        int x = 7;
        int y = Math.max(46, (g.guiHeight() - panelH) / 2);

        blitScaled(g, PANEL, x, y, panelW, panelH, 128, 160);
        g.drawCenteredString(mc.font, Component.literal("★ BINGO ★"), x + panelW / 2, y + 7, 0xFFFFD86A);

        int completed = 0;
        int gridX = x + 8;
        int gridY = y + 22;
        for (int i = 0; i < goals.size() && i < size * size; i++) {
            GoalData goal = goals.get(i);
            int col = i % size;
            int row = i / size;
            int itemX = gridX + col * cell;
            int itemY = gridY + row * cell;
            int background = goal.completed() ? 0xC832A85A : 0xC0261931;
            g.fill(itemX, itemY, itemX + 18, itemY + 18, 0xFF8C66A0);
            g.fill(itemX + 1, itemY + 1, itemX + 17, itemY + 17, background);
            renderIcon(g, iconFor(goal), itemX + 1, itemY + 1, 16);
            if (goal.completed()) {
                completed++;
                g.drawString(mc.font, "✓", itemX + 10, itemY + 9, 0xFF9CFFAA, true);
            } else if (goal.required() > 1) {
                String count = goal.progress() + "/" + goal.required();
                g.pose().pushPose();
                g.pose().scale(0.5f, 0.5f, 1.0f);
                g.drawString(mc.font, count, (itemX + 1) * 2, (itemY + 12) * 2, 0xFFFFFFFF, true);
                g.pose().popPose();
            }
        }
        g.drawCenteredString(mc.font, Component.literal(completed + "/" + goals.size()),
                x + panelW / 2, y + panelH - 10, 0xFFF2EAF7);
    }

    private static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return "";
        try { return new String(DECODER.decode(encoded), StandardCharsets.UTF_8); }
        catch (IllegalArgumentException ignored) { return encoded; }
    }
    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
