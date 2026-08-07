package com.emilyextacy.emotes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public final class FavoritesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("emilyemotes.json");
    private static FavoritesConfig INSTANCE;

    private Set<String> favorites = new LinkedHashSet<>();

    public static FavoritesConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    private static FavoritesConfig load() {
        if (Files.exists(FILE)) {
            try {
                String json = Files.readString(FILE, StandardCharsets.UTF_8);
                FavoritesConfig cfg = GSON.fromJson(json, FavoritesConfig.class);
                if (cfg != null) {
                    if (cfg.favorites == null) cfg.favorites = new LinkedHashSet<>();
                    return cfg;
                }
            } catch (Exception ignored) {
            }
        }
        return new FavoritesConfig();
    }

    public boolean isFavorite(String name) {
        return favorites.contains(name);
    }

    public void toggle(String name) {
        if (!favorites.add(name)) {
            favorites.remove(name);
        }
        save();
    }

    private void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }
}
