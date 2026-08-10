package com.randomchunk;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class DayCounterState extends PersistentState {
    private int totalDays = 0;
    private int currentHeight = 1000;
    private boolean initialized = false;
    private long lastDay = 0;
    private boolean hundredDaysAchievementGranted = false;

    private static final String KEY = "randomchunk_state";

    public static DayCounterState get(MinecraftServer server) {
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        return manager.getOrCreate(new PersistentState.Type<>(
                DayCounterState::new,
                DayCounterState::fromNbt,
                null
        ), KEY);
    }

    private static DayCounterState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        DayCounterState state = new DayCounterState();
        state.readNbt(nbt, lookup);
        return state;
    }

    public static void load(MinecraftServer server) {}
    public static void save(MinecraftServer server) { get(server).markDirty(); }

    // Убраны аннотации @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        nbt.putInt("totalDays", totalDays);
        nbt.putInt("currentHeight", currentHeight);
        nbt.putBoolean("initialized", initialized);
        nbt.putLong("lastDay", lastDay);
        nbt.putBoolean("hundredDaysAchievementGranted", hundredDaysAchievementGranted);
        return nbt;
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        totalDays = nbt.getInt("totalDays");
        currentHeight = nbt.getInt("currentHeight");
        initialized = nbt.getBoolean("initialized");
        lastDay = nbt.getLong("lastDay");
        hundredDaysAchievementGranted = nbt.getBoolean("hundredDaysAchievementGranted");
    }

    public int getTotalDays() { return totalDays; }
    public void incrementDays() { totalDays++; markDirty(); }
    public int getCurrentHeight() { return currentHeight; }
    public void setCurrentHeight(int height) { this.currentHeight = height; markDirty(); }
    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean val) { this.initialized = val; markDirty(); }
    public long getLastDay() { return lastDay; }
    public void setLastDay(long day) { this.lastDay = day; markDirty(); }
    public boolean isHundredDaysAchievementGranted() { return hundredDaysAchievementGranted; }
    public void setHundredDaysAchievementGranted(boolean val) { this.hundredDaysAchievementGranted = val; markDirty(); }
}