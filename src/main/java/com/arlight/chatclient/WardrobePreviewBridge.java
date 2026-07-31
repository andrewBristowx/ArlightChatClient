package com.arlight.chatclient;

import net.minecraft.client.player.AbstractClientPlayer;

import java.lang.reflect.Method;

/** Optional bridge: the wardrobe still opens when the Curios cosmetic mod is absent. */
final class WardrobePreviewBridge {
    private static Method begin;
    private static Method end;
    private static boolean resolved;

    static void begin(AbstractClientPlayer player, String cosmeticId) {
        resolve();
        if (begin == null || cosmeticId == null || cosmeticId.isBlank()) return;
        try { begin.invoke(null, player, cosmeticId); } catch (ReflectiveOperationException ignored) { }
    }

    static void end(AbstractClientPlayer player) {
        resolve();
        if (end == null) return;
        try { end.invoke(null, player); } catch (ReflectiveOperationException ignored) { }
    }

    private static void resolve() {
        if (resolved) return;
        resolved = true;
        try {
            Class<?> type = Class.forName("com.arlight.cosmeticscurios.client.WardrobePreviewController");
            begin = type.getMethod("begin", AbstractClientPlayer.class, String.class);
            end = type.getMethod("end", AbstractClientPlayer.class);
        } catch (ReflectiveOperationException ignored) {
            begin = null;
            end = null;
        }
    }

    private WardrobePreviewBridge() { }
}
