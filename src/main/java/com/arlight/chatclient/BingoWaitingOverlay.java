package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/** Controla la pantalla persistente de la cola del Bingo. */
public final class BingoWaitingOverlay {
    private static boolean visible;
    private static int players = 1;
    private static int maxPlayers = 10;
    private static int countdown = -1;
    private static Object connectionIdentity;

    private BingoWaitingOverlay() { }

    public static int players() { return players; }
    public static int maxPlayers() { return maxPlayers; }
    public static int countdown() { return countdown; }

    public static void accept(String command) {
        if (command == null) return;
        String[] parts = command.split("\\|", -1);
        if (parts.length == 0) return;
        if ("HIDE".equalsIgnoreCase(parts[0])) {
            hide();
            return;
        }
        if (!"SHOW".equalsIgnoreCase(parts[0]) && !"UPDATE".equalsIgnoreCase(parts[0])) return;

        players = parts.length > 1 ? parseInt(parts[1], 1) : 1;
        maxPlayers = parts.length > 2 ? parseInt(parts[2], 10) : 10;
        countdown = parts.length > 3 ? parseInt(parts[3], -1) : -1;
        visible = true;

        // Si se regresa a la cola después de un fallo de preparación, quitamos su overlay.
        BingoPreparationOverlay.accept("HIDE");

        Minecraft mc = Minecraft.getInstance();
        connectionIdentity = mc.getConnection();
        HudVisibilityController.acquire("bingo_waiting");
        if (!(mc.screen instanceof BingoWaitingScreen)) mc.setScreen(new BingoWaitingScreen());
    }

    public static void leaveQueue() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) mc.getConnection().sendCommand("bingo leave");
        hide();
    }

    public static void hide() {
        visible = false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof BingoWaitingScreen) mc.setScreen(null);
        HudVisibilityController.release("bingo_waiting");
    }

    public static void validateConnection() {
        Minecraft mc = Minecraft.getInstance();
        Object current = mc.getConnection();
        if (connectionIdentity != null && connectionIdentity != current) hide();
        connectionIdentity = current;
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        visible = false;
        players = 1;
        maxPlayers = 10;
        countdown = -1;
        connectionIdentity = null;
        HudVisibilityController.release("bingo_waiting");
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
