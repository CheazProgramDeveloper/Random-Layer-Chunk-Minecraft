package com.randomchunk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RandomChunkClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ничего не делаем (кровать и так есть)
    }
}