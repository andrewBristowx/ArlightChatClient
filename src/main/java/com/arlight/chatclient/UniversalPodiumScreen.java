package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

/** Podio 3D reutilizable por todos los minijuegos Arlight. */
public final class UniversalPodiumScreen extends Screen {
    private final UniversalPodiumOverlay.Data data;
    private int ticks;

    public UniversalPodiumScreen(UniversalPodiumOverlay.Data data) {
        super(Component.literal("Resultados de " + data.gameName()));
        this.data = data;
    }

    @Override
    protected void init() {
        HudVisibilityController.acquire("universal_podium");
        int buttonY = height - 44;
        int buttonW = Math.min(180, Math.max(125, width / 5));
        int gap = 12;
        int left = width / 2 - buttonW - gap / 2;
        MinigameWaitingButton replay = new MinigameWaitingButton(
                left, buttonY, buttonW, 28, Component.literal("Volver a jugar"),
                false, accent(), this::requeue);
        replay.active = !data.requeueCommand().isBlank();
        addRenderableWidget(replay);
        addRenderableWidget(new MinigameWaitingButton(
                width / 2 + gap / 2, buttonY, buttonW, 28, Component.literal("Salir al lobby"),
                true, accent(), this::closeScreen));
    }

    @Override
    public void tick() {
        ticks++;
        HudVisibilityController.keepHidden();
        if (ticks >= data.displayTicks()) closeScreen();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        HudVisibilityController.keepHidden();
        Minecraft minecraft = Minecraft.getInstance();
        graphics.blit(background(), 0, 0, 0, 0, width, height, width, height);
        graphics.fill(0, 0, width, height, 0x62000000);
        drawConfetti(graphics);

        int headerH = Math.max(62, height / 9);
        graphics.fill(0, 0, width, headerH, 0xC80A0B1B);
        graphics.fill(0, headerH - 4, width, headerH, accent());
        drawScaledCentered(graphics, minecraft, data.gameName(), width / 2, 16, 1.75F, 0xFFFFFFFF);
        PodiumEmoteRenderer.drawCentered(graphics, minecraft.font, data.subtitle(),
                width / 2, 43, 0xFFE8DFF2, false);

        int modelBottom = Math.min(height - 122, Math.max(headerH + 238, Math.round(height * 0.69F)));
        int spacing = Math.max(150, Math.min(270, width / 4));
        drawEntry(graphics, byPlace(2), width / 2 - spacing, modelBottom + 12, 2, partialTick);
        drawEntry(graphics, byPlace(1), width / 2, modelBottom - 10, 1, partialTick);
        drawEntry(graphics, byPlace(3), width / 2 + spacing, modelBottom + 20, 3, partialTick);

        drawViewerStats(graphics, minecraft);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawEntry(GuiGraphics graphics, UniversalPodiumOverlay.Entry entry,
                           int x, int bottomY, int place, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int medal = place == 1 ? 0xFFFFD35A : place == 2 ? 0xFFD6D8E2 : 0xFFD68A57;
        int scale = place == 1 ? 72 : place == 2 ? 61 : 57;
        int frameW = place == 1 ? 170 : 148;
        int frameTop = Math.max(82, bottomY - (place == 1 ? 205 : 180));

        // Marco translúcido: deja visible el fondo y evita los antiguos bloques de podio.
        graphics.fill(x - frameW / 2, frameTop, x + frameW / 2, bottomY + 43, 0x3A0B0A18);
        graphics.fill(x - frameW / 2, frameTop, x - frameW / 2 + 3, bottomY + 43, medal);
        graphics.fill(x + frameW / 2 - 3, frameTop, x + frameW / 2, bottomY + 43, medal);
        graphics.fill(x - frameW / 2 + 9, bottomY + 1, x + frameW / 2 - 9, bottomY + 5, medal);

        // Medalla compacta en la esquina: identifica el puesto sin cubrir la cabeza,
        // el nombre ni los emotes/cosméticos del ganador.
        drawEntryBadge(graphics, x - frameW / 2 + 22, frameTop + 12, place);

        if (entry != null && entry.uuid() != null && minecraft.level != null) {
            Player entity = minecraft.level.getPlayerByUUID(entry.uuid());
            if (entity != null) renderWinner(graphics, entity, x, bottomY, scale, place, partialTick);
        }

        String name = entry == null ? "—" : entry.name();
        PodiumEmoteRenderer.drawCentered(graphics, minecraft.font,
                place + ".º  " + name, x, bottomY + 10, medal, false);
        String role = place == 1 ? "CAMPEÓN" : place == 2 ? "FINALISTA" : "TERCER PUESTO";
        PodiumEmoteRenderer.drawCentered(graphics, minecraft.font,
                role, x, bottomY + 24, 0xFFF6F0FA, false);
    }

    private void renderWinner(GuiGraphics graphics, Player entity, int x, int bottomY,
                              int scale, int place, float partialTick) {
        ItemStack oldMain = entity.getMainHandItem().copy();
        ItemStack oldOff = entity.getOffhandItem().copy();
        boolean oldCrouching = entity.isShiftKeyDown();
        float oldYRot = entity.getYRot();
        float oldXRot = entity.getXRot();
        float oldBodyRot = entity.yBodyRot;
        float oldHeadRot = entity.yHeadRot;
        try {
            // Presentación limpia: manos vacías, cuerpo completo y mirada al frente.
            entity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            entity.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            entity.setShiftKeyDown(false);
            // El renderer de inventario considera 180 grados como la pose frontal.
            // Con cero grados el jugador enseñaba la espalda en el podio.
            entity.setYRot(180.0F);
            entity.setXRot(-2.0F);
            entity.yBodyRot = 180.0F;
            entity.yHeadRot = 180.0F;

            float animation = ticks + partialTick + place * 5.0F;
            float bob = (float) Math.sin(animation * 0.12F) * (place == 1 ? 3.0F : 1.6F);
            if ((ticks + place * 9) % 48 == 0) {
                entity.swing(place == 2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, true);
            }
            Quaternionf bodyRotation = new Quaternionf()
                    .rotateZ((float) Math.PI)
                    .rotateY((float) Math.toRadians(Math.sin(animation * 0.045F) * 4.0F));
            Quaternionf cameraRotation = new Quaternionf().rotateX((float) Math.toRadians(-3.0F));
            InventoryScreen.renderEntityInInventory(
                    graphics, (float) x, (float) (bottomY + bob), (float) scale,
                    new Vector3f(0.0F, 0.0F, 0.0F), bodyRotation, cameraRotation, entity);
        } finally {
            entity.setItemSlot(EquipmentSlot.MAINHAND, oldMain);
            entity.setItemSlot(EquipmentSlot.OFFHAND, oldOff);
            entity.setShiftKeyDown(oldCrouching);
            entity.setYRot(oldYRot);
            entity.setXRot(oldXRot);
            entity.yBodyRot = oldBodyRot;
            entity.yHeadRot = oldHeadRot;
        }
    }

    private void drawEntryBadge(GuiGraphics graphics, int x, int y, int place) {
        int size = place == 1 ? 34 : 30;
        PodiumEmoteRenderer.drawEntryBadge(graphics, "", place, x, y, size);
    }

    private void drawViewerStats(GuiGraphics graphics, Minecraft minecraft) {
        List<UniversalPodiumOverlay.Stat> stats = data.viewerStats();
        if (stats.isEmpty()) return;
        int boxW = Math.min(300, Math.max(230, width / 4));
        int boxH = 34 + Math.min(5, stats.size()) * 17;
        int x = 18;
        int y = height - boxH - 18;
        graphics.fill(x, y, x + boxW, y + boxH, 0xD00A0B1B);
        graphics.fill(x, y, x + 4, y + boxH, accent());
        String placement = data.viewerPlace() > 0 ? "TU PUESTO: #" + data.viewerPlace() : "TUS ESTADÍSTICAS";
        graphics.drawString(minecraft.font, Component.literal(placement), x + 12, y + 10, accent(), false);
        int rowY = y + 28;
        for (int i = 0; i < Math.min(5, stats.size()); i++) {
            UniversalPodiumOverlay.Stat stat = stats.get(i);
            int iconOffset = PodiumEmoteRenderer.drawStatIcon(graphics, stat.icon(), x + 12, rowY - 3, 14);
            PodiumEmoteRenderer.draw(graphics, minecraft.font, stat.label(),
                    x + 12 + iconOffset, rowY, 0xFFDAD1E4, false);
            int valueWidth = PodiumEmoteRenderer.width(minecraft.font, stat.value(), 14);
            PodiumEmoteRenderer.draw(graphics, minecraft.font, stat.value(),
                    x + boxW - valueWidth - 12, rowY, 0xFFFFFFFF, false);
            rowY += 17;
        }
    }

    private void drawConfetti(GuiGraphics graphics) {
        int count = Math.min(46, Math.max(18, width / 28));
        for (int i = 0; i < count; i++) {
            int x = Math.floorMod(i * 97 + ticks * (1 + i % 3), Math.max(1, width));
            int y = Math.floorMod(i * 53 + ticks * (2 + i % 2), Math.max(1, height - 55));
            int color = switch (i % 4) {
                case 0 -> accent();
                case 1 -> 0xFFFFD35A;
                case 2 -> 0xFFD6D8E2;
                default -> 0xFFFF8BCB;
            };
            graphics.fill(x, y, x + 3 + i % 3, y + 6, color);
        }
    }

    private void drawScaledCentered(GuiGraphics graphics, Minecraft minecraft, String text,
                                    int centerX, int y, float scale, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, y, 0);
        graphics.pose().scale(scale, scale, 1.0F);
        PodiumEmoteRenderer.drawCentered(graphics, minecraft.font, text, 0, 0, color, false);
        graphics.pose().popPose();
    }

    private UniversalPodiumOverlay.Entry byPlace(int place) {
        return data.entries().stream().filter(entry -> entry.place() == place).findFirst().orElse(null);
    }

    private int accent() {
        return switch (data.gameId().toLowerCase()) {
            case "tntrun" -> 0xFFFF537A;
            case "parkour" -> 0xFF65E6FF;
            case "buildbattle" -> 0xFFFFC857;
            case "bingo" -> 0xFFE775FF;
            case "skywars" -> 0xFF72B7FF;
            case "luckyblockislands" -> 0xFFFFDF55;
            default -> 0xFFFF8BCB;
        };
    }

    private ResourceLocation background() {
        String path = switch (data.gameId().toLowerCase()) {
            case "tntrun" -> "tntrun_waiting_background.png";
            case "parkour" -> "parkour_waiting_background.png";
            case "buildbattle" -> "buildbattle_waiting_background.png";
            case "bingo" -> "bingo_waiting_background.png";
            case "skywars" -> "skywars_waiting_background.png";
            case "luckyblockislands" -> "lucky_waiting_background.png";
            default -> "bingo_waiting_background.png";
        };
        return ResourceLocation.fromNamespaceAndPath(ArlightChatClient.MOD_ID, "textures/loading/" + path);
    }

    private void requeue() {
        Minecraft minecraft = Minecraft.getInstance();
        String command = data.requeueCommand();
        closeScreen();
        if (!command.isBlank() && minecraft.getConnection() != null) minecraft.getConnection().sendCommand(command);
    }

    private void closeScreen() { Minecraft.getInstance().setScreen(null); }

    @Override
    public void removed() {
        HudVisibilityController.release("universal_podium");
        super.removed();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && ticks < 40) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }
    @Override public boolean isPauseScreen() { return false; }
}
