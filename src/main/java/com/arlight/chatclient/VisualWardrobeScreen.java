package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class VisualWardrobeScreen extends Screen {
    private static final int BG = 0xF0120D1D;
    private static final int CARD = 0xE3261B35;
    private static final int CARD_HOVER = 0xEE4A285F;
    private static final int PINK = 0xFFFF72C8;
    private static final int CYAN = 0xFF74E7FF;
    private static final int MUTED = 0xFFBEB2C8;
    private static final int GREEN = 0xFF6FFFA0;
    private static final String[] FILTERS = {"TODOS", "HEAD", "CHEST", "LEGS", "FEET", "BACK", "TAIL", "AURA", "PASOS", "PET"};
    private static final String[] FILTER_LABELS = {"TODOS", "CABEZA", "TORSO", "PIERNAS", "PIES", "ESPALDA", "COLA", "AURA", "PASOS", "MASCOTA"};

    private int selectedIndex;
    private int filterIndex;
    private int scrollRow;
    private int hoveredIndex = -1;
    private float mannequinYaw = 0.0F;
    private boolean rotatingMannequin;
    private double lastDragX;
    private boolean positionEditing;
    private float[] petPosition = new float[]{0.0F, 0.0F, 0.0F};

    public VisualWardrobeScreen() {
        super(Component.literal("Ropero Pony0n"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0xB0080710);
        int x = 24;
        int y = 18;
        int w = width - 48;
        int h = height - 36;
        g.fill(x, y, x + w, y + h, BG);
        g.fill(x, y, x + 5, y + h, PINK);
        g.drawString(font, "✦ MI ROPERO", x + 20, y + 14, 0xFFFFFFFF, true);
        g.drawString(font, "Pasa el mouse para revisar y haz clic para seleccionar", x + 20, y + 30, MUTED, false);

        List<WardrobeState.Entry> all = WardrobeState.entries();
        if (all.isEmpty()) {
            g.drawCenteredString(font, "Todavía no tienes cosméticos reclamados", width / 2, height / 2, 0xFFFFFFFF);
            super.render(g, mouseX, mouseY, partialTick);
            return;
        }

        int filterY = y + 48;
        int filterX = x + 18;
        for (int i = 0; i < FILTERS.length; i++) {
            String label = FILTER_LABELS[i];
            int fw = Math.max(i == 0 ? 58 : 54, font.width(label) + 16);
            boolean active = i == filterIndex;
            boolean over = hit(mouseX, mouseY, filterX, filterY, fw, 20);
            g.fill(filterX, filterY, filterX + fw, filterY + 20,
                    active ? 0xEE7D407E : over ? 0xCC58336B : 0x99402A50);
            g.drawCenteredString(font, label, filterX + fw / 2, filterY + 6,
                    active ? 0xFFFFFFFF : MUTED);
            filterX += fw + 5;
        }

        List<WardrobeState.Entry> shown = filtered(all);
        if (shown.isEmpty()) {
            g.drawCenteredString(font, "No tienes cosméticos en esta categoría", width / 2, height / 2, MUTED);
            super.render(g, mouseX, mouseY, partialTick);
            return;
        }

        selectedIndex = Math.max(0, Math.min(selectedIndex, shown.size() - 1));
        int previewX = x + 18;
        int previewY = y + 76;
        int previewW = Math.max(300, w * 43 / 100);
        int bottom = y + h - 18;
        g.fill(previewX, previewY, previewX + previewW, bottom, CARD);

        hoveredIndex = positionEditing ? -1 : gridHover(shown, mouseX, mouseY, previewX + previewW + 18, previewY, x + w - 18, bottom);
        int detailIndex = positionEditing ? selectedIndex : (hoveredIndex >= 0 ? hoveredIndex : selectedIndex);
        detailIndex = Math.max(0, Math.min(detailIndex, shown.size() - 1));
        WardrobeState.Entry detail = shown.get(detailIndex);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // La selección se aplica solo al maniquí. El inventario real y Curios
            // se restauran inmediatamente después del renderizado.
            WardrobePreviewBridge.begin(mc.player, detail.id());
            ItemStack oldMain = mc.player.getMainHandItem().copy();
            ItemStack oldOff = mc.player.getOffhandItem().copy();
            mc.player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            mc.player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            try {
                // Giro real de 360 grados. El método FollowsMouse de Minecraft
                // limita la rotación a un arco pequeño mediante atan(), por eso
                // usamos la sobrecarga con quaternion explícito.
                float yawRadians = (float) Math.toRadians(mannequinYaw);
                Quaternionf bodyRotation = new Quaternionf()
                        .rotateZ((float) Math.PI)
                        .rotateY(yawRadians);
                Quaternionf cameraRotation = new Quaternionf();
                boolean companionPreview = detail.slot().toUpperCase(Locale.ROOT).contains("COMPANION")
                        || detail.slot().toUpperCase(Locale.ROOT).contains("PET");
                float entityScale = companionPreview ? 75.0F
                        : detail.slot().equalsIgnoreCase("HEAD") ? 72.0F : 69.0F;
                g.fill(previewX + previewW / 2 - 64, bottom - 137,
                        previewX + previewW / 2 + 64, bottom - 132, 0x553F2A52);
                InventoryScreen.renderEntityInInventory(g,
                        (float) (previewX + previewW / 2),
                        (float) (bottom - 138),
                        entityScale,
                        new Vector3f(0.0F, 0.0F, 0.0F),
                        bodyRotation, cameraRotation, mc.player);
            } finally {
                mc.player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, oldMain);
                mc.player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND, oldOff);
                WardrobePreviewBridge.end(mc.player);
            }
            g.drawCenteredString(font, "Arrastra el maniquí para girarlo", previewX + previewW / 2,
                    bottom - 126, MUTED);
        }

        ItemStack icon = stackFor(detail.id());
        int itemBoxY = bottom - 104;
        g.fill(previewX + 18, itemBoxY, previewX + previewW - 18, bottom - 16, 0xB8151021);
        if (!icon.isEmpty()) {
            g.pose().pushPose();
            g.pose().translate(previewX + 38, itemBoxY + 18, 0);
            g.pose().scale(3.0F, 3.0F, 1.0F);
            g.renderItem(icon, 0, 0);
            g.pose().popPose();
        }
        g.drawString(font, detail.name(), previewX + 96, itemBoxY + 15, PINK, true);
        g.drawString(font, "Ranura: " + detail.slot(), previewX + 96, itemBoxY + 32, CYAN, false);
        g.drawString(font, "Rareza: " + detail.rarity(), previewX + 96, itemBoxY + 47, 0xFFFFD66B, false);
        g.drawWordWrap(font, Component.literal(detail.description()), previewX + 96, itemBoxY + 63,
                previewW - 126, MUTED);

        int gridX = previewX + previewW + 18;
        int gridW = x + w - 18 - gridX;
        int columns = Math.max(4, gridW / 76);
        int cell = 70;
        int gap = 6;
        int gridTop = previewY;
        boolean selectedCompanion = WardrobePreviewBridge.isCompanion(shown.get(selectedIndex).id());
        int reservedBottom = selectedCompanion ? 88 : 52;
        int rowsVisible = Math.max(1, (bottom - gridTop - reservedBottom) / (cell + gap));
        int maxScroll = Math.max(0, (shown.size() + columns - 1) / columns - rowsVisible);
        scrollRow = Math.max(0, Math.min(scrollRow, maxScroll));

        if (!positionEditing) {
            for (int local = 0; local < rowsVisible * columns; local++) {
                int idx = (scrollRow * columns) + local;
                if (idx >= shown.size()) break;
                int col = local % columns;
                int row = local / columns;
                int cx = gridX + col * (cell + gap);
                int cy = gridTop + row * (cell + gap);
                WardrobeState.Entry e = shown.get(idx);
                boolean over = idx == hoveredIndex;
                boolean selected = idx == selectedIndex;
                String active = WardrobeState.equipped().get(e.slot());
                boolean equipped = Objects.equals(active, e.id());
                int color = selected ? 0xEE6C3977 : over ? CARD_HOVER : 0xCC261B35;
                g.fill(cx, cy, cx + cell, cy + cell, color);
                g.fill(cx, cy, cx + cell, cy + 3, equipped ? GREEN : rarityColor(e.rarity()));
                ItemStack stack = stackFor(e.id());
                if (!stack.isEmpty()) {
                    g.pose().pushPose();
                    g.pose().translate(cx + 15, cy + 9, 0);
                    g.pose().scale(2.0F, 2.0F, 1.0F);
                    g.renderItem(stack, 0, 0);
                    g.pose().popPose();
                }
                drawCardName(g, e.name(), cx, cy, cell, equipped ? GREEN : 0xFFFFFFFF);
                if (equipped) g.drawString(font, "✓", cx + cell - 11, cy + 6, GREEN, true);
            }
        }

        WardrobeState.Entry selected = shown.get(selectedIndex);
        String active = WardrobeState.equipped().get(selected.slot());
        boolean equipped = Objects.equals(active, selected.id());
        int buttonY = bottom - 38;
        int buttonW = Math.min(150, gridW / 2);
        int clearX = gridX + buttonW + 8;
        int clearW = Math.min(130, Math.max(96, gridW - buttonW - 8));
        if (positionEditing && selectedCompanion) {
            renderPositionEditor(g, selected, gridX, previewY, gridW, bottom, mouseX, mouseY);
        } else {
            button(g, gridX, buttonY, buttonW, 26, equipped ? "Quitar" : "Equipar", mouseX, mouseY);
            button(g, clearX, buttonY, clearW, 26, "Quitar todos", mouseX, mouseY);
            if (selectedCompanion) {
                button(g, gridX, buttonY - 34, Math.min(gridW, buttonW + clearW + 8), 26,
                        "Ajustar posición de mascota", mouseX, mouseY);
            } else if (maxScroll > 0) {
                g.drawString(font, "Rueda del mouse: " + (scrollRow + 1) + "/" + (maxScroll + 1),
                        gridX, buttonY - 14, MUTED, false);
            }
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = 24;
        int y = 18;
        int w = width - 48;
        int h = height - 36;
        int filterY = y + 48;
        int filterX = x + 18;
        for (int i = 0; i < FILTERS.length; i++) {
            String label = FILTER_LABELS[i];
            int fw = Math.max(i == 0 ? 58 : 54, font.width(label) + 16);
            if (hit(mouseX, mouseY, filterX, filterY, fw, 20)) {
                filterIndex = i;
                selectedIndex = 0;
                scrollRow = 0;
                positionEditing = false;
                return true;
            }
            filterX += fw + 5;
        }

        List<WardrobeState.Entry> shown = filtered(WardrobeState.entries());
        if (shown.isEmpty()) return super.mouseClicked(mouseX, mouseY, button);
        int previewX = x + 18;
        int previewY = y + 76;
        int previewW = Math.max(300, w * 43 / 100);
        int bottom = y + h - 18;
        if (hit(mouseX, mouseY, previewX, previewY, previewW, bottom - previewY - 112)) {
            rotatingMannequin = true;
            lastDragX = mouseX;
            return true;
        }
        int gridX = previewX + previewW + 18;
        int gridW = x + w - 18 - gridX;
        int columns = Math.max(4, gridW / 76);
        int cell = 70;
        int gap = 6;
        WardrobeState.Entry selected = shown.get(selectedIndex);
        boolean selectedCompanion = WardrobePreviewBridge.isCompanion(selected.id());
        if (positionEditing && selectedCompanion) {
            if (handlePositionEditorClick(selected, gridX, previewY, gridW, bottom, mouseX, mouseY)) return true;
        }
        int reservedBottom = selectedCompanion ? 88 : 52;
        int rowsVisible = Math.max(1, (bottom - previewY - reservedBottom) / (cell + gap));
        int localHover = positionEditing ? -1 : gridHover(shown, mouseX, mouseY, gridX, previewY, x + w - 18, bottom);
        if (localHover >= 0) {
            selectedIndex = localHover;
            positionEditing = false;
            return true;
        }

        int buttonY = bottom - 38;
        int buttonW = Math.min(150, gridW / 2);
        int clearX = gridX + buttonW + 8;
        int clearW = Math.min(130, Math.max(96, gridW - buttonW - 8));
        if (selectedCompanion && hit(mouseX, mouseY, gridX, buttonY - 34,
                Math.min(gridW, buttonW + clearW + 8), 26)) {
            positionEditing = true;
            petPosition = WardrobePreviewBridge.position(selected.id());
            return true;
        }
        if (hit(mouseX, mouseY, gridX, buttonY, buttonW, 26)) {
            WardrobeState.Entry e = shown.get(selectedIndex);
            String active = WardrobeState.equipped().get(e.slot());
            send(Objects.equals(active, e.id()) ? "UNEQUIP|" + e.slot() : "EQUIP|" + WardrobeState.enc(e.id()));
            return true;
        }
        if (hit(mouseX, mouseY, clearX, buttonY, clearW, 26)) {
            send("CLEARALL|all");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (rotatingMannequin) {
            mannequinYaw += (float) (mouseX - lastDragX);
            mannequinYaw = wrapDegrees(mannequinYaw);
            lastDragX = mouseX;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        rotatingMannequin = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (vertical > 0) scrollRow--;
        if (vertical < 0) scrollRow++;
        return true;
    }

    private static float wrapDegrees(float value) {
        value %= 360.0F;
        if (value < 0.0F) value += 360.0F;
        return value;
    }

    private List<WardrobeState.Entry> filtered(List<WardrobeState.Entry> source) {
        if (filterIndex == 0) return source;
        String wanted = FILTERS[filterIndex];
        List<WardrobeState.Entry> out = new ArrayList<>();
        for (WardrobeState.Entry e : source) {
            String slot = e.slot().toUpperCase(Locale.ROOT);
            if (slot.equals(wanted) || (wanted.equals("PASOS") && slot.equals("TRAIL"))
                    || (wanted.equals("PET") && (slot.contains("PET") || slot.contains("COMPANION")))) {
                out.add(e);
            }
        }
        return out;
    }

    private int gridHover(List<WardrobeState.Entry> shown, double mouseX, double mouseY,
                          int gridX, int gridY, int gridRight, int bottom) {
        int gridW = gridRight - gridX;
        int columns = Math.max(4, gridW / 76);
        int cell = 70;
        int gap = 6;
        int reservedBottom = WardrobePreviewBridge.isCompanion(shown.get(selectedIndex).id()) ? 88 : 52;
        int rowsVisible = Math.max(1, (bottom - gridY - reservedBottom) / (cell + gap));
        for (int local = 0; local < rowsVisible * columns; local++) {
            int idx = scrollRow * columns + local;
            if (idx >= shown.size()) break;
            int cx = gridX + (local % columns) * (cell + gap);
            int cy = gridY + (local / columns) * (cell + gap);
            if (hit(mouseX, mouseY, cx, cy, cell, cell)) return idx;
        }
        return -1;
    }

    private static ItemStack stackFor(String id) {
        try {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath("arlightcosmeticscurios", id);
            var item = BuiltInRegistries.ITEM.get(key);
            return item == null ? ItemStack.EMPTY : new ItemStack(item);
        } catch (Exception ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static int rarityColor(String rarity) {
        String value = rarity == null ? "" : rarity.toUpperCase(Locale.ROOT);
        if (value.contains("LEGEND")) return 0xFFFFB347;
        if (value.contains("EPIC")) return 0xFFD36BFF;
        if (value.contains("RARE")) return 0xFF62B7FF;
        return 0xFFBEB2C8;
    }

    private void renderPositionEditor(GuiGraphics g, WardrobeState.Entry selected,
                                      int x, int y, int w, int bottom,
                                      double mouseX, double mouseY) {
        g.fill(x, y, x + w, bottom - 46, 0xE31C1427);
        g.drawCenteredString(font, "AJUSTAR POSICIÓN", x + w / 2, y + 18, PINK);
        g.drawCenteredString(font, selected.name(), x + w / 2, y + 34, 0xFFFFFFFF);
        g.drawCenteredString(font, "Los cambios se ven en vivo en el maniquí y en el mundo",
                x + w / 2, y + 50, MUTED);

        int rowX = x + Math.max(12, (w - 280) / 2);
        int rowW = Math.min(280, w - 24);
        int rowY = y + 78;
        positionRow(g, rowX, rowY, rowW, "Lateral X", petPosition[0], mouseX, mouseY);
        positionRow(g, rowX, rowY + 38, rowW, "Altura Y", petPosition[1], mouseX, mouseY);
        positionRow(g, rowX, rowY + 76, rowW, "Distancia Z", petPosition[2], mouseX, mouseY);

        g.drawCenteredString(font, "X: izquierda/derecha  •  Y: abajo/arriba  •  Z: cerca/lejos",
                x + w / 2, rowY + 116, MUTED);
        int actionY = bottom - 82;
        int actionW = Math.min(132, (w - 10) / 2);
        button(g, x, actionY, actionW, 26, "Restablecer", mouseX, mouseY);
        button(g, x + actionW + 10, actionY, actionW, 26, "Guardar y volver", mouseX, mouseY);
    }

    private void positionRow(GuiGraphics g, int x, int y, int w, String label, float value,
                             double mouseX, double mouseY) {
        int control = 46;
        button(g, x, y, control, 26, "−", mouseX, mouseY);
        g.fill(x + control + 6, y, x + w - control - 6, y + 26, 0xAA2A1D38);
        g.drawCenteredString(font, label + ": " + String.format(Locale.ROOT, "%+.2f", value),
                x + w / 2, y + 9, 0xFFFFFFFF);
        button(g, x + w - control, y, control, 26, "+", mouseX, mouseY);
    }

    private boolean handlePositionEditorClick(WardrobeState.Entry selected,
                                              int x, int y, int w, int bottom,
                                              double mouseX, double mouseY) {
        int rowX = x + Math.max(12, (w - 280) / 2);
        int rowW = Math.min(280, w - 24);
        int rowY = y + 78;
        int control = 46;
        final float step = 0.05F;
        for (int axis = 0; axis < 3; axis++) {
            int cy = rowY + axis * 38;
            if (hit(mouseX, mouseY, rowX, cy, control, 26)) {
                petPosition = adjust(selected.id(), axis, -step);
                return true;
            }
            if (hit(mouseX, mouseY, rowX + rowW - control, cy, control, 26)) {
                petPosition = adjust(selected.id(), axis, step);
                return true;
            }
        }
        int actionY = bottom - 82;
        int actionW = Math.min(132, (w - 10) / 2);
        if (hit(mouseX, mouseY, x, actionY, actionW, 26)) {
            petPosition = WardrobePreviewBridge.resetPosition(selected.id());
            return true;
        }
        if (hit(mouseX, mouseY, x + actionW + 10, actionY, actionW, 26)) {
            WardrobePreviewBridge.savePositions();
            positionEditing = false;
            return true;
        }
        return false;
    }

    private static float[] adjust(String cosmeticId, int axis, float amount) {
        return WardrobePreviewBridge.adjustPosition(cosmeticId,
                axis == 0 ? amount : 0.0F,
                axis == 1 ? amount : 0.0F,
                axis == 2 ? amount : 0.0F);
    }

    private void drawCardName(GuiGraphics g, String value, int x, int y, int cell, int color) {
        List<net.minecraft.util.FormattedCharSequence> lines = font.split(
                Component.literal(value == null ? "" : value), cell - 8);
        if (lines.isEmpty()) return;
        int firstY = lines.size() > 1 ? y + 45 : y + 51;
        g.drawCenteredString(font, lines.get(0), x + cell / 2, firstY, color);
        if (lines.size() > 1) {
            g.drawCenteredString(font, lines.get(1), x + cell / 2, y + 55, color);
        }
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String text, double mouseX, double mouseY) {
        boolean over = hit(mouseX, mouseY, x, y, w, h);
        g.fill(x, y, x + w, y + h, over ? 0xDD7D407E : 0xCC4B2D5D);
        g.drawCenteredString(font, text, x + w / 2, y + 9, 0xFFFFFFFF);
    }

    private static boolean hit(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private static void send(String value) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        String[] parts = value.split("\\|", 2);
        if (parts.length < 2) return;
        if (parts[0].equals("EQUIP")) {
            connection.sendCommand("ropero equip " + WardrobeState.decPublic(parts[1]));
        } else if (parts[0].equals("UNEQUIP")) {
            connection.sendCommand("ropero unequip " + parts[1]);
        } else if (parts[0].equals("CLEARALL")) {
            connection.sendCommand("ropero clearall");
        }
    }

    @Override
    public void removed() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) WardrobePreviewBridge.end(mc.player);
        WardrobePreviewBridge.savePositions();
        super.removed();
    }

    @Override
    public void onClose() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) WardrobePreviewBridge.end(mc.player);
        WardrobePreviewBridge.savePositions();
        super.onClose();
    }

    @Override public boolean isPauseScreen() { return false; }
    @Override public void renderBackground(GuiGraphics g, int x, int y, float partialTick) { }
}
