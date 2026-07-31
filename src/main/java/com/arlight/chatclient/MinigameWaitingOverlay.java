package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Estado compartido de las salas visuales de los nuevos minijuegos. */
public final class MinigameWaitingOverlay {
    public enum Game {
        TNTRUN(
                "TNTRUN",
                "tntrun leave",
                "textures/loading/tntrun_waiting_background.png",
                0xFFFF7D74,
                0xFFFFB0D0,
                "Corre sin detenerte: el suelo desaparece bajo tus pies.",
                List.of(
                        "Los bloques desaparecen después de pisarlos.",
                        "Recoge mejoras que aparecen aleatoriamente.",
                        "El último jugador que siga en pie gana."
                )),
        PARKOUR(
                "PARKOUR",
                "parkour leave",
                "textures/loading/parkour_waiting_background.png",
                0xFF78C8FF,
                0xFFB2A2FF,
                "Avanza por el recorrido y alcanza tantos checkpoints como puedas.",
                List.of(
                        "La partida tiene un tiempo límite.",
                        "Al caer vuelves al último checkpoint alcanzado.",
                        "Gana quien tenga más checkpoints al terminar el tiempo."
                )),
        BUILDBATTLE(
                "BUILD BATTLE",
                "buildbattle leave",
                "textures/loading/buildbattle_waiting_background.png",
                0xFFFF91BA,
                0xFFBA96FF,
                "Propón un tema, construye y vota por la mejor creación.",
                List.of(
                        "Cada jugador puede escribir una propuesta de tema.",
                        "Todos votan y construyen el tema ganador.",
                        "Después se visitan las parcelas y se elige al ganador."
                ));

        private final String title;
        private final String leaveCommand;
        private final ResourceLocation background;
        private final int accent;
        private final int accentSecondary;
        private final String summary;
        private final List<String> help;

        Game(String title, String leaveCommand, String backgroundPath,
             int accent, int accentSecondary, String summary, List<String> help) {
            this.title = title;
            this.leaveCommand = leaveCommand;
            this.background = ResourceLocation.fromNamespaceAndPath(
                    ArlightChatClient.MOD_ID, backgroundPath);
            this.accent = accent;
            this.accentSecondary = accentSecondary;
            this.summary = summary;
            this.help = help;
        }

        public String title() { return title; }
        public String leaveCommand() { return leaveCommand; }
        public ResourceLocation background() { return background; }
        public int accent() { return accent; }
        public int accentSecondary() { return accentSecondary; }
        public String summary() { return summary; }
        public List<String> help() { return help; }
    }

    private static boolean visible;
    private static Game game = Game.TNTRUN;
    private static int players = 1;
    private static int maxPlayers = 12;
    private static int countdown = -1;
    private static String status = "ESPERANDO_JUGADORES";
    private static List<String> playerNames = List.of();
    private static Object connectionIdentity;

    private MinigameWaitingOverlay() { }

    public static Game game() { return game; }
    public static int players() { return players; }
    public static int maxPlayers() { return maxPlayers; }
    public static int countdown() { return countdown; }
    public static String status() { return status; }
    public static List<String> playerNames() { return playerNames; }

    public static void accept(Game incomingGame, String command) {
        if (incomingGame == null || command == null) return;
        String[] parts = command.split("\\|", -1);
        if (parts.length == 0) return;

        if ("HIDE".equalsIgnoreCase(parts[0])) {
            if (game == incomingGame) hide();
            return;
        }
        if (!"SHOW".equalsIgnoreCase(parts[0]) && !"UPDATE".equalsIgnoreCase(parts[0])) return;

        game = incomingGame;
        players = parts.length > 1 ? parseInt(parts[1], 1) : 1;
        maxPlayers = parts.length > 2 ? parseInt(parts[2], 12) : 12;
        countdown = parts.length > 3 ? parseInt(parts[3], -1) : -1;
        status = parts.length > 4 && !parts[4].isBlank()
                ? parts[4] : "ESPERANDO_JUGADORES";
        playerNames = parseNames(parts.length > 5 ? parts[5] : "");
        visible = true;

        Minecraft minecraft = Minecraft.getInstance();
        connectionIdentity = minecraft.getConnection();
        HudVisibilityController.acquire("new_minigame_waiting");
        // No interrumpir el chat: al cerrarlo, la siguiente actualización
        // de la cola volverá a mostrar la sala automáticamente.
        if (minecraft.screen instanceof ChatScreen) return;
        if (!(minecraft.screen instanceof MinigameWaitingScreen screen)
                || screen.game() != incomingGame) {
            minecraft.setScreen(new MinigameWaitingScreen(incomingGame));
        }
    }

    public static void leaveQueue() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            minecraft.getConnection().sendCommand(game.leaveCommand());
        }
        hide();
    }

    public static void hide() {
        visible = false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof MinigameWaitingScreen) minecraft.setScreen(null);
        HudVisibilityController.release("new_minigame_waiting");
    }

    public static void validateConnection() {
        Minecraft minecraft = Minecraft.getInstance();
        Object current = minecraft.getConnection();
        if (connectionIdentity != null && connectionIdentity != current) hide();
        connectionIdentity = current;
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        visible = false;
        players = 1;
        maxPlayers = 12;
        countdown = -1;
        status = "ESPERANDO_JUGADORES";
        playerNames = List.of();
        connectionIdentity = null;
        HudVisibilityController.release("new_minigame_waiting");
    }

    private static List<String> parseNames(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        String[] split = raw.split(",");
        List<String> names = new ArrayList<>();
        for (String value : split) {
            String name = value.trim();
            if (!name.isBlank() && name.length() <= 32) names.add(name);
            if (names.size() >= 12) break;
        }
        return Collections.unmodifiableList(names);
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
