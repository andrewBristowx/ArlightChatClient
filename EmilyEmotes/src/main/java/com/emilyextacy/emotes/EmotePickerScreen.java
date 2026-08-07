package com.emilyextacy.emotes;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmotePickerScreen extends Screen {
    private static final int PINK = 0xFFFF78BC;
    private static final int PINK_DARK = 0xFF9F3F72;
    private static final int WHITE = 0xFFF8F4F8;
    private static final int PANEL = 0xE817121C;
    private static final int PANEL_HOVER = 0xEF342437;
    private static final int BORDER = 0xFF7A4263;

    private static final int GRID_TOP = 96;
    private static final int BOTTOM_SPACE = 34;
    private static final int TILE_W = 64;
    private static final int TILE_H = 54;
    private static final int GAP = 6;

    private final Screen parent;
    private final FavoritesConfig favorites = FavoritesConfig.get();

    private TextFieldWidget searchBox;
    private List<StreamotesBridge.EmoteRef> allEmotes = new ArrayList<>();
    private List<StreamotesBridge.EmoteRef> filtered = new ArrayList<>();
    private SourceFilter sourceFilter = SourceFilter.ALL;
    private int scrollRow;
    private int refreshTicks;
    private StreamotesBridge.EmoteRef hovered;

    public EmotePickerScreen(Screen parent) {
        super(Text.literal("EmilyEmotes"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int searchWidth = Math.min(270, Math.max(180, width - 120));
        searchBox = new TextFieldWidget(textRenderer, width / 2 - searchWidth / 2, 39, searchWidth, 20, Text.literal("Buscar emote"));
        searchBox.setPlaceholder(Text.literal("Buscar emote...").styled(s -> s.withColor(0xB7AAB4)));
        searchBox.setChangedListener(value -> applyFilter());
        addDrawableChild(searchBox);
        setInitialFocus(searchBox);

        int buttonY = 66;
        String[] labels = {"Todos", "♥", "7TV", "Twitch", "BTTV", "FFZ"};
        SourceFilter[] filters = SourceFilter.values();
        int bw = 48;
        int total = labels.length * bw + (labels.length - 1) * 3;
        int bx = width / 2 - total / 2;
        for (int i = 0; i < labels.length; i++) {
            SourceFilter f = filters[i];
            addDrawableChild(ButtonWidget.builder(Text.literal(labels[i]), button -> {
                sourceFilter = f;
                scrollRow = 0;
                applyFilter();
            }).dimensions(bx + i * (bw + 3), buttonY, bw, 20).build());
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("↻"), button -> reloadFromStreamotes())
                .dimensions(width - 50, 10, 20, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("×"), button -> close())
                .dimensions(width - 27, 10, 20, 20).build());

        reloadFromStreamotes();
    }

    private void reloadFromStreamotes() {
        allEmotes = StreamotesBridge.snapshot();
        applyFilter();
    }

    private void applyFilter() {
        String query = searchBox == null ? "" : searchBox.getText().trim().toLowerCase(Locale.ROOT);
        List<StreamotesBridge.EmoteRef> result = new ArrayList<>();
        for (StreamotesBridge.EmoteRef emote : allEmotes) {
            if (!query.isEmpty() && !emote.name().toLowerCase(Locale.ROOT).contains(query)) continue;
            if (!sourceFilter.accepts(emote, favorites)) continue;
            result.add(emote);
        }
        filtered = result;
        clampScroll();
    }

    private void clampScroll() {
        int columns = columns();
        int visibleRows = visibleRows();
        int totalRows = (filtered.size() + columns - 1) / columns;
        int maxScroll = Math.max(0, totalRows - visibleRows);
        scrollRow = Math.max(0, Math.min(scrollRow, maxScroll));
    }

    @Override
    public void tick() {
        super.tick();
        if (searchBox != null) searchBox.tick();
        if (allEmotes.isEmpty() && ++refreshTicks >= 40) {
            refreshTicks = 0;
            reloadFromStreamotes();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.fill(0, 0, width, height, 0xB20B0810);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("♡ EmilyEmotes ♡").styled(s -> s.withBold(true).withColor(PINK)), width / 2, 13, PINK);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Galería de Streamotes • clic = insertar • clic derecho = favorito"), width / 2, 26, 0xFFD7CBD4);

        hovered = null;
        if (!StreamotesBridge.isAvailable()) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Streamotes no está instalado en este cliente."), width / 2, height / 2 - 5, 0xFFFF7777);
        } else if (filtered.isEmpty()) {
            String msg = allEmotes.isEmpty()
                    ? "Esperando a que Streamotes termine de cargar los emotes..."
                    : "No hay emotes que coincidan con este filtro.";
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(msg), width / 2, height / 2 - 5, WHITE);
        } else {
            renderGrid(context, mouseX, mouseY);
        }

        if (hovered != null) {
            String fav = favorites.isFavorite(hovered.name()) ? " ♥" : "";
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal(hovered.name() + "  •  " + hovered.source() + fav),
                    width / 2, height - 19, WHITE);
        } else {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal(filtered.size() + " emotes • filtro: " + sourceFilter.label),
                    width / 2, height - 19, 0xFFC8BBC5);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderGrid(DrawContext context, int mouseX, int mouseY) {
        int columns = columns();
        int rows = visibleRows();
        int gridWidth = columns * TILE_W + (columns - 1) * GAP;
        int startX = width / 2 - gridWidth / 2;
        int startIndex = scrollRow * columns;
        int maxVisible = rows * columns;

        for (int local = 0; local < maxVisible; local++) {
            int index = startIndex + local;
            if (index >= filtered.size()) break;

            int col = local % columns;
            int row = local / columns;
            int x = startX + col * (TILE_W + GAP);
            int y = GRID_TOP + row * (TILE_H + GAP);
            StreamotesBridge.EmoteRef emote = filtered.get(index);

            boolean over = mouseX >= x && mouseX < x + TILE_W && mouseY >= y && mouseY < y + TILE_H;
            if (over) hovered = emote;

            drawPanel(context, x, y, TILE_W, TILE_H, over ? PANEL_HOVER : PANEL, over ? PINK : BORDER);
            emote.requestTexture();

            Text preview = emote.preview();
            int previewWidth = Math.max(1, textRenderer.getWidth(preview));
            float scale = 2.0f;
            context.getMatrices().push();
            context.getMatrices().translate(x + TILE_W / 2.0f, y + 7.0f, 0.0f);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.drawTextWithShadow(textRenderer, preview, -previewWidth / 2, 0, WHITE);
            context.getMatrices().pop();

            String name = compact(emote.name(), 10);
            int nameColor = favorites.isFavorite(emote.name()) ? PINK : WHITE;
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(name), x + TILE_W / 2, y + TILE_H - 13, nameColor);

            String tag = sourceTag(emote.sourceKey());
            context.drawTextWithShadow(textRenderer, Text.literal(tag), x + 3, y + 3, 0xFFBDA7B6);
            if (favorites.isFavorite(emote.name())) {
                context.drawTextWithShadow(textRenderer, Text.literal("♥"), x + TILE_W - 10, y + 3, PINK);
            }
        }
    }

    private static void drawPanel(DrawContext context, int x, int y, int w, int h, int fill, int border) {
        context.fill(x, y, x + w, y + h, fill);
        context.fill(x, y, x + w, y + 1, border);
        context.fill(x, y + h - 1, x + w, y + h, border);
        context.fill(x, y, x + 1, y + h, border);
        context.fill(x + w - 1, y, x + w, y + h, border);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        StreamotesBridge.EmoteRef clicked = entryAt(mouseX, mouseY);
        if (clicked != null) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                favorites.toggle(clicked.name());
                applyFilter();
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                insertIntoChat(clicked.name());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount != 0) {
            scrollRow -= amount > 0 ? 1 : -1;
            clampScroll();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private StreamotesBridge.EmoteRef entryAt(double mouseX, double mouseY) {
        if (mouseY < GRID_TOP) return null;
        int columns = columns();
        int rows = visibleRows();
        int gridWidth = columns * TILE_W + (columns - 1) * GAP;
        int startX = width / 2 - gridWidth / 2;
        int startIndex = scrollRow * columns;

        for (int local = 0; local < rows * columns; local++) {
            int index = startIndex + local;
            if (index >= filtered.size()) break;
            int col = local % columns;
            int row = local / columns;
            int x = startX + col * (TILE_W + GAP);
            int y = GRID_TOP + row * (TILE_H + GAP);
            if (mouseX >= x && mouseX < x + TILE_W && mouseY >= y && mouseY < y + TILE_H) {
                return filtered.get(index);
            }
        }
        return null;
    }

    private void insertIntoChat(String name) {
        if (client != null) {
            client.setScreen(new ChatScreen(name + " "));
        }
    }

    private int columns() {
        return Math.max(3, Math.min(9, (width - 28) / (TILE_W + GAP)));
    }

    private int visibleRows() {
        return Math.max(1, (height - GRID_TOP - BOTTOM_SPACE) / (TILE_H + GAP));
    }

    private static String compact(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, Math.max(1, max - 1)) + "…";
    }

    private static String sourceTag(String source) {
        return switch (source) {
            case "7TV" -> "7";
            case "TWITCH" -> "T";
            case "BTTV" -> "B";
            case "FFZ" -> "F";
            default -> "•";
        };
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    private enum SourceFilter {
        ALL("Todos"),
        FAVORITES("Favoritos"),
        SEVEN_TV("7TV"),
        TWITCH("Twitch"),
        BTTV("BTTV"),
        FFZ("FFZ");

        private final String label;

        SourceFilter(String label) {
            this.label = label;
        }

        boolean accepts(StreamotesBridge.EmoteRef emote, FavoritesConfig favorites) {
            return switch (this) {
                case ALL -> true;
                case FAVORITES -> favorites.isFavorite(emote.name());
                case SEVEN_TV -> "7TV".equals(emote.sourceKey());
                case TWITCH -> "TWITCH".equals(emote.sourceKey());
                case BTTV -> "BTTV".equals(emote.sourceKey());
                case FFZ -> "FFZ".equals(emote.sourceKey());
            };
        }
    }
}
