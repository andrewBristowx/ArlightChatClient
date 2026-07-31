package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ArlightChatClient.MOD_ID, value = Dist.CLIENT)
public final class ClientScreenEvents {
    private ClientScreenEvents() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen inventoryScreen
                && !ModList.get().isLoaded("arlightcosmeticscurios")) {
            addCosmeticArmorPanel(event, inventoryScreen);
        }
        if (!(event.getScreen() instanceof ChatScreen chatScreen)) return;

        EditBox chatInput = chatScreen.getFocused() instanceof EditBox editBox
                ? editBox : null;
        List<EmoteDefinition> availableEmotes = ServerEmoteRegistry.getEmotes();

        Map<String, String> groupNames = new LinkedHashMap<>();
        Map<String, List<EmoteDefinition>> groupedEmotes = new LinkedHashMap<>();
        for (EmoteDefinition emote : availableEmotes) {
            groupNames.putIfAbsent(emote.groupId(), emote.groupName());
            groupedEmotes.computeIfAbsent(emote.groupId(), ignored -> new ArrayList<>()).add(emote);
        }
        if (groupedEmotes.isEmpty()) return;

        int buttonWidth = 76;
        int buttonX = Math.max(4, chatScreen.width - 160);
        int buttonY = Math.max(4, chatScreen.height - 40);
        int columns = 5;
        int iconSize = 28;
        int iconGap = 3;
        int panelWidth = columns * iconSize + (columns + 1) * iconGap;

        // Las categorías permanecen apiladas, pero se compactan verticalmente para
        // que el panel empiece debajo del scoreboard sin crecer hacia arriba.
        // Se mantienen tres filas de emotes y la paginación para no reducir su tamaño.
        int groupColumns = 1;
        int groupRows = groupedEmotes.size();
        int groupButtonHeight = 14;
        int groupGap = 2;
        int groupAreaHeight = groupRows * groupButtonHeight + (groupRows + 1) * groupGap;
        int visibleRows = 3;
        int itemsPerPage = columns * visibleRows;
        int pagerHeight = 14;
        int headerHeight = 20;
        int panelHeight = headerHeight + groupAreaHeight + pagerHeight
                + visibleRows * iconSize + (visibleRows + 1) * iconGap;
        int panelX = Math.max(4, chatScreen.width - 84 - panelWidth);
        int panelY = Math.max(4, buttonY - panelHeight - 4);

        List<AbstractWidget> permanentPanelWidgets = new ArrayList<>();
        Map<String, List<AbstractWidget>> widgetsByGroup = new LinkedHashMap<>();
        Map<String, TransparentButton> groupButtons = new LinkedHashMap<>();
        boolean[] panelOpen = {false};
        String[] selectedGroup = {groupedEmotes.keySet().iterator().next()};
        int[] selectedPage = {0};
        Runnable[] refresh = new Runnable[1];

        PanelBackgroundWidget background = new PanelBackgroundWidget(
                panelX, panelY, panelWidth, panelHeight,
                Component.translatable("arlightchatclient.button.emotes"));
        background.visible = false;
        permanentPanelWidgets.add(background);
        event.addListener(background);

        int groupIndex = 0;
        int groupButtonWidth = (panelWidth - (groupColumns + 1) * groupGap) / groupColumns;
        for (Map.Entry<String, String> entry : groupNames.entrySet()) {
            String groupId = entry.getKey();
            int column = groupIndex % groupColumns;
            int row = groupIndex / groupColumns;
            int x = panelX + groupGap + column * (groupButtonWidth + groupGap);
            int y = panelY + headerHeight + groupGap + row * groupButtonHeight;
            TransparentButton groupButton = new TransparentButton(
                    x, y, groupButtonWidth, groupButtonHeight,
                    Component.literal(entry.getValue()), false, () -> {
                        selectedGroup[0] = groupId;
                        selectedPage[0] = 0;
                        refresh[0].run();
                        focusChatLater(chatScreen, chatInput);
                    });
            groupButton.visible = false;
            groupButtons.put(groupId, groupButton);
            permanentPanelWidgets.add(groupButton);
            event.addListener(groupButton);
            groupIndex++;
        }

        int pagerY = panelY + headerHeight + groupAreaHeight;
        int emoteStartY = pagerY + pagerHeight;
        for (Map.Entry<String, List<EmoteDefinition>> group : groupedEmotes.entrySet()) {
            List<AbstractWidget> groupWidgets = new ArrayList<>();
            widgetsByGroup.put(group.getKey(), groupWidgets);
            for (int index = 0; index < group.getValue().size(); index++) {
                EmoteDefinition emote = group.getValue().get(index);
                int column = index % columns;
                int row = (index % itemsPerPage) / columns;
                int x = panelX + iconGap + column * (iconSize + iconGap);
                int y = emoteStartY + iconGap + row * (iconSize + iconGap);
                Component icon = Component.literal(emote.glyph());
                if (emote.animated()) {
                    icon = icon.copy().withStyle(net.minecraft.ChatFormatting.YELLOW);
                }
                TransparentButton emoteButton = new TransparentButton(
                        x, y, iconSize, iconSize, icon, true, () -> {
                            if (chatInput == null) return;
                            String separator = chatInput.getValue().isEmpty()
                                    || chatInput.getValue().endsWith(" ") ? "" : " ";
                            chatInput.insertText(separator + emote.alias() + " ");
                            panelOpen[0] = false;
                            refresh[0].run();
                            focusChatLater(chatScreen, chatInput);
                        });
                emoteButton.setTooltip(Tooltip.create(Component.literal(emote.alias())));
                emoteButton.visible = false;
                groupWidgets.add(emoteButton);
                event.addListener(emoteButton);
            }
        }

        TransparentButton previousPage = new TransparentButton(
                panelX + iconGap, pagerY, 24, 14, Component.literal("<"), false, () -> {
                    if (selectedPage[0] > 0) selectedPage[0]--;
                    refresh[0].run();
                    focusChatLater(chatScreen, chatInput);
                });
        TransparentButton nextPage = new TransparentButton(
                panelX + panelWidth - iconGap - 24, pagerY, 24, 14, Component.literal(">"), false, () -> {
                    int count = groupedEmotes.getOrDefault(selectedGroup[0], List.of()).size();
                    int pages = Math.max(1, (count + itemsPerPage - 1) / itemsPerPage);
                    if (selectedPage[0] + 1 < pages) selectedPage[0]++;
                    refresh[0].run();
                    focusChatLater(chatScreen, chatInput);
                });
        TransparentButton pageIndicator = new TransparentButton(
                panelX + 32, pagerY, panelWidth - 64, 14, Component.literal("1/1"), false, () -> {});
        previousPage.visible = false;
        nextPage.visible = false;
        pageIndicator.visible = false;
        permanentPanelWidgets.add(previousPage);
        permanentPanelWidgets.add(nextPage);
        permanentPanelWidgets.add(pageIndicator);
        event.addListener(previousPage);
        event.addListener(nextPage);
        event.addListener(pageIndicator);

        refresh[0] = () -> {
            permanentPanelWidgets.forEach(widget -> widget.visible = panelOpen[0]);
            groupButtons.forEach((groupId, button) -> button.setMessage(Component.literal(
                    (groupId.equals(selectedGroup[0]) ? "• " : "") + groupNames.get(groupId))));

            List<AbstractWidget> selectedWidgets = widgetsByGroup.getOrDefault(selectedGroup[0], List.of());
            int pages = Math.max(1, (selectedWidgets.size() + itemsPerPage - 1) / itemsPerPage);
            selectedPage[0] = Math.max(0, Math.min(selectedPage[0], pages - 1));
            int start = selectedPage[0] * itemsPerPage;
            int end = Math.min(start + itemsPerPage, selectedWidgets.size());

            widgetsByGroup.forEach((groupId, widgets) -> {
                for (int i = 0; i < widgets.size(); i++) {
                    widgets.get(i).visible = panelOpen[0]
                            && groupId.equals(selectedGroup[0])
                            && i >= start && i < end;
                }
            });

            pageIndicator.setMessage(Component.literal((selectedPage[0] + 1) + "/" + pages));
            previousPage.visible = panelOpen[0] && selectedPage[0] > 0;
            nextPage.visible = panelOpen[0] && selectedPage[0] + 1 < pages;
            pageIndicator.visible = panelOpen[0] && pages > 1;
        };

        event.addListener(new TransparentButton(
                buttonX, buttonY, buttonWidth, 20,
                Component.translatable("arlightchatclient.button.emotes"), false, () -> {
                    panelOpen[0] = !panelOpen[0];
                    refresh[0].run();
                    focusChatLater(chatScreen, chatInput);
                }));
    }

    private static void addCosmeticArmorPanel(ScreenEvent.Init.Post event, InventoryScreen screen) {
        int inventoryLeft = (screen.width - 176) / 2;
        int x = Math.max(4, inventoryLeft - 30);
        int y = Math.max(8, (screen.height - 166) / 2 + 18);
        String[] labels = {"H", "C", "L", "B"};
        String[] names = {"Capucha/cabeza del conjunto", "Pecho y mangas del conjunto",
                "Falda/pantalón del conjunto", "Botas y medias del conjunto"};
        for (int i = 0; i < labels.length; i++) {
            TransparentButton slot = new TransparentButton(
                    x, y + i * 24, 22, 22, Component.literal(labels[i]), false,
                    ClientScreenEvents::openCosmeticWardrobe);
            slot.setTooltip(Tooltip.create(Component.literal(names[i]
                    + "\nRanura visual independiente.\nNo ocupa armadura normal.\nClic: abrir conjuntos")));
            event.addListener(slot);
        }
    }

    private static void openCosmeticWardrobe() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            minecraft.setScreen(null);
            minecraft.getConnection().sendCommand("cosmeticos armadura");
        }
    }

    private static void focusChatLater(ChatScreen chatScreen, EditBox chatInput) {
        if (chatInput == null) return;
        Minecraft.getInstance().tell(() -> {
            chatScreen.setFocused(chatInput);
            chatInput.setFocused(true);
        });
    }
}
