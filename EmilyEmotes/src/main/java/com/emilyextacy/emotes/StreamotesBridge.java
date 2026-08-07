package com.emilyextacy.emotes;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Puente por reflexión hacia Streamotes. No depende de clases internas de
 * Streamotes durante la compilación, por lo que es más resistente a cambios
 * menores entre builds del mod.
 */
public final class StreamotesBridge {
    private static final String REGISTRY_CLASS = "xeed.mc.streamotes.emoticon.EmoticonRegistry";

    private StreamotesBridge() {
    }

    public static boolean isAvailable() {
        try {
            Class.forName(REGISTRY_CLASS);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static List<EmoteRef> snapshot() {
        List<EmoteRef> result = new ArrayList<>();
        try {
            Class<?> registry = Class.forName(REGISTRY_CLASS);
            Method getEmotes = registry.getMethod("getEmotes");
            Object value = getEmotes.invoke(null);
            if (!(value instanceof Collection<?> collection)) {
                return result;
            }

            for (Object raw : new ArrayList<>(collection)) {
                EmoteRef ref = wrap(raw);
                if (ref != null) {
                    result.add(ref);
                }
            }
        } catch (Throwable ignored) {
            // La pantalla mostrará un estado vacío y permitirá reintentar.
        }

        result.sort(Comparator.comparing(EmoteRef::name, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    private static EmoteRef wrap(Object raw) {
        try {
            Class<?> type = raw.getClass();
            Method getName = type.getMethod("getName");
            Method getSource = type.getMethod("getSource");
            Method getPreview = type.getMethod("getPreview");
            Method requestTexture = type.getMethod("requestTexture");

            String name = String.valueOf(getName.invoke(raw));
            String source = String.valueOf(getSource.invoke(raw));
            Object previewObject = getPreview.invoke(raw);

            Text preview;
            if (previewObject instanceof Text text) {
                Style style = text.getStyle();
                preview = Text.literal(name).setStyle(style);
            } else {
                preview = Text.literal(name);
            }

            return new EmoteRef(raw, name, source, preview, requestTexture);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public record EmoteRef(Object handle, String name, String source, Text preview, Method requestTextureMethod) {
        public void requestTexture() {
            try {
                requestTextureMethod.invoke(handle);
            } catch (Throwable ignored) {
            }
        }

        public String sourceKey() {
            String s = source == null ? "" : source.toUpperCase(Locale.ROOT);
            if (s.contains("7TV")) return "7TV";
            if (s.contains("TWITCH")) return "TWITCH";
            if (s.contains("BTTV")) return "BTTV";
            if (s.contains("FFZ") || s.contains("FRANKER")) return "FFZ";
            return s;
        }
    }
}
