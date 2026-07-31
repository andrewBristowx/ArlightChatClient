package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/** Controla la pantalla persistente de la cola de SkyWars. */
public final class SkyWarsWaitingOverlay {
    private static boolean visible;
    private static int players = 1;
    private static int maxPlayers = 8;
    private static String map = "Por elegir";
    private static Object connectionIdentity;

    private SkyWarsWaitingOverlay() { }

    public static boolean isVisible() { return visible; }
    public static int players() { return players; }
    public static int maxPlayers() { return maxPlayers; }
    public static String map() { return map; }

    public static void accept(String command) {
        if (command == null) return;
        String[] p = command.split("\\|", -1);
        if (p.length == 0) return;
        if ("HIDE".equalsIgnoreCase(p[0])) {
            hide();
            return;
        }
        if (!"SHOW".equalsIgnoreCase(p[0]) && !"UPDATE".equalsIgnoreCase(p[0])) return;

        players = p.length > 1 ? parseInt(p[1], 1) : 1;
        maxPlayers = p.length > 2 ? parseInt(p[2], 8) : 8;
        map = p.length > 3 && !p[3].isBlank() ? p[3] : "Por elegir";
        visible = true;

        Minecraft mc = Minecraft.getInstance();
        connectionIdentity = mc.getConnection();
        HudVisibilityController.acquire("skywars_waiting");
        if (!(mc.screen instanceof SkyWarsWaitingScreen)) {
            mc.setScreen(new SkyWarsWaitingScreen());
        }
    }

    public static void leaveQueue() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) mc.getConnection().sendCommand("sw leave");
        hide();
    }

    public static void hide() {
        visible = false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof SkyWarsWaitingScreen) mc.setScreen(null);
        HudVisibilityController.release("skywars_waiting");
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        visible = false;
        players = 1;
        maxPlayers = 8;
        map = "Por elegir";
        connectionIdentity = null;
        HudVisibilityController.release("skywars_waiting");
    }

    public static void validateConnection() {
        Minecraft mc = Minecraft.getInstance();
        Object current = mc.getConnection();
        if (connectionIdentity != null && connectionIdentity != current) hide();
        connectionIdentity = current;
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
