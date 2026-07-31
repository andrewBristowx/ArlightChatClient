package com.arlight.chatclient;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Imita F1 mientras una pantalla cinematográfica está activa. Xaero's Minimap respeta
 * hideGui, por lo que también desaparece junto al chat, hotbar y overlays normales.
 */
public final class HudVisibilityController {
    private static final Set<String> owners = new HashSet<>();
    private static Boolean previousHideGui;
    private static Object connectionIdentity;

    private HudVisibilityController() { }

    public static void acquire(String owner) {
        Minecraft mc = Minecraft.getInstance();
        checkConnection(mc);
        if (owners.add(owner) && previousHideGui == null) previousHideGui = mc.options.hideGui;
        mc.options.hideGui = true;
    }

    public static void keepHidden() {
        Minecraft mc = Minecraft.getInstance();
        checkConnection(mc);
        if (!owners.isEmpty()) mc.options.hideGui = true;
    }

    public static void release(String owner) {
        owners.remove(owner);
        if (owners.isEmpty()) restore();
    }

    public static void reset() {
        owners.clear();
        restore();
        connectionIdentity = Minecraft.getInstance().getConnection();
    }

    private static void checkConnection(Minecraft mc) {
        Object current = mc.getConnection();
        if (connectionIdentity != null && connectionIdentity != current) {
            owners.clear();
            restore();
        }
        connectionIdentity = current;
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        reset();
    }

    private static void restore() {
        if (previousHideGui != null) {
            Minecraft.getInstance().options.hideGui = previousHideGui;
            previousHideGui = null;
        }
    }
}
