package com.arlight.chatclient;

import java.util.Locale;

public enum CosmeticSlot {
    HEAD,
    OUTFIT,
    BACK,
    TAIL,
    SHOULDER,
    AURA,
    TRAIL;

    public static CosmeticSlot parse(String raw) {
        if (raw == null) return null;
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
