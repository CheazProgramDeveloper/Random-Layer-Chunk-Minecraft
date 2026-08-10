package com.randomchunk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class Config {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("randomchunk.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int chunkX = 0;
    public int chunkZ = 0;
    public int layersPerDay = 3;
    public boolean enabled = true;
    public boolean useAllBlocks = true;
    public String[] blockFilter = {};
    public int baseHeight = 200;        // высота первого слоя (поверхность)
    public int worldHeight = 1000;      // максимальная высота мира (по умолчанию 1000)

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                instance = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                instance = new Config();
            }
        } else {
            instance = new Config();
            save();
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(instance, writer);
        } catch (IOException ignored) {}
    }
}