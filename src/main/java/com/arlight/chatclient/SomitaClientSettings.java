package com.arlight.chatclient;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

/** Preferencia local de efectos de Somita. Se lee una vez al iniciar el cliente. */
public final class SomitaClientSettings {
    public enum Mode {
        FULL(1.0F), REDUCED(0.45F), OFF(0.0F);

        private final float multiplier;

        Mode(float multiplier) {
            this.multiplier = multiplier;
        }

        public float multiplier() {
            return multiplier;
        }
    }

    private static boolean loaded;
    private static Mode mode = Mode.FULL;

    private SomitaClientSettings() { }

    public static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        Path file = Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config").resolve("arlightchatclient-somita.properties");
        Properties properties = new Properties();
        try {
            Files.createDirectories(file.getParent());
            if (Files.isRegularFile(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    properties.load(reader);
                }
            } else {
                properties.setProperty("effects", "full");
                properties.setProperty("description", "full | reduced | off");
                try (Writer writer = Files.newBufferedWriter(file)) {
                    properties.store(writer, "Opciones locales de Somita");
                }
            }
        } catch (IOException ignored) {
            return;
        }
        String value = properties.getProperty("effects", "full")
                .trim().toUpperCase(Locale.ROOT);
        mode = switch (value) {
            case "OFF", "DISABLED", "DESACTIVADAS" -> Mode.OFF;
            case "REDUCED", "LOW", "REDUCIDAS" -> Mode.REDUCED;
            default -> Mode.FULL;
        };
    }

    public static float effectMultiplier() {
        ensureLoaded();
        return mode.multiplier();
    }

    public static Mode mode() {
        ensureLoaded();
        return mode;
    }
}
