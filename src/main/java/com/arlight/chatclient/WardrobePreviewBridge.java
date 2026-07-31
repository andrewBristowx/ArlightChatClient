package com.arlight.chatclient;

import net.minecraft.client.player.AbstractClientPlayer;

import java.lang.reflect.Method;

/** Optional bridge: el ropero sigue abriendo aunque el mod Curios no esté presente. */
final class WardrobePreviewBridge {
    private static Method begin;
    private static Method end;
    private static Method isCompanion;
    private static Method position;
    private static Method adjustPosition;
    private static Method resetPosition;
    private static Method savePositions;
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

    static boolean isCompanion(String cosmeticId) {
        resolve();
        if (isCompanion == null || cosmeticId == null) return false;
        try { return Boolean.TRUE.equals(isCompanion.invoke(null, cosmeticId)); }
        catch (ReflectiveOperationException ignored) { return false; }
    }

    static float[] position(String cosmeticId) {
        resolve();
        if (position == null || cosmeticId == null) return new float[]{0, 0, 0};
        try {
            Object value = position.invoke(null, cosmeticId);
            return value instanceof float[] values && values.length >= 3
                    ? new float[]{values[0], values[1], values[2]}
                    : new float[]{0, 0, 0};
        } catch (ReflectiveOperationException ignored) {
            return new float[]{0, 0, 0};
        }
    }

    static float[] adjustPosition(String cosmeticId, float dx, float dy, float dz) {
        resolve();
        if (adjustPosition == null || cosmeticId == null) return position(cosmeticId);
        try {
            Object value = adjustPosition.invoke(null, cosmeticId, dx, dy, dz);
            return value instanceof float[] values && values.length >= 3
                    ? new float[]{values[0], values[1], values[2]}
                    : position(cosmeticId);
        } catch (ReflectiveOperationException ignored) {
            return position(cosmeticId);
        }
    }

    static float[] resetPosition(String cosmeticId) {
        resolve();
        if (resetPosition == null || cosmeticId == null) return new float[]{0, 0, 0};
        try {
            Object value = resetPosition.invoke(null, cosmeticId);
            return value instanceof float[] values && values.length >= 3
                    ? new float[]{values[0], values[1], values[2]}
                    : new float[]{0, 0, 0};
        } catch (ReflectiveOperationException ignored) {
            return new float[]{0, 0, 0};
        }
    }

    static void savePositions() {
        resolve();
        if (savePositions == null) return;
        try { savePositions.invoke(null); } catch (ReflectiveOperationException ignored) { }
    }

    private static void resolve() {
        if (resolved) return;
        resolved = true;
        try {
            Class<?> type = Class.forName("com.arlight.cosmeticscurios.client.WardrobePreviewController");
            begin = type.getMethod("begin", AbstractClientPlayer.class, String.class);
            end = type.getMethod("end", AbstractClientPlayer.class);
            isCompanion = type.getMethod("isCompanion", String.class);
            position = type.getMethod("position", String.class);
            adjustPosition = type.getMethod("adjustPosition", String.class, float.class, float.class, float.class);
            resetPosition = type.getMethod("resetPosition", String.class);
            savePositions = type.getMethod("savePositions");
        } catch (ReflectiveOperationException ignored) {
            begin = null;
            end = null;
            isCompanion = null;
            position = null;
            adjustPosition = null;
            resetPosition = null;
            savePositions = null;
        }
    }

    private WardrobePreviewBridge() { }
}
