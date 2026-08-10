package com.randomchunk;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.*;

public class DayCounterEvents {
    private static final Random RANDOM = new Random();
    private static double timeMultiplier = 1.0;
    private static double timeAccumulator = 0.0;
    private static boolean hasSpawnedHostileThisNight = false;

    public static final GameRules.Key<GameRules.BooleanRule> RANDOM_CHUNK_ENABLED =
            GameRuleRegistry.register("randomChunk", GameRules.Category.MISC,
                    GameRuleFactory.createBooleanRule(false));

    private static final EntityType<?>[] PEACEFUL_MOBS = {
            EntityType.SHEEP, EntityType.COW, EntityType.CHICKEN, EntityType.PIG,
            EntityType.RABBIT, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE
    };

    private static final EntityType<?>[] HOSTILE_MOBS = {
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CREEPER,
            EntityType.ENDERMAN, EntityType.WITCH, EntityType.SLIME
    };

    private static final ItemStack[] CHEST_LOOT = {
            new ItemStack(Items.IRON_INGOT, 8),
            new ItemStack(Items.GOLD_INGOT, 4),
            new ItemStack(Items.DIAMOND, 2),
            new ItemStack(Items.EMERALD, 3),
            new ItemStack(Items.APPLE, 16),
            new ItemStack(Items.BREAD, 8),
            new ItemStack(Items.COOKED_BEEF, 8),
            new ItemStack(Items.IRON_SWORD),
            new ItemStack(Items.IRON_PICKAXE),
            new ItemStack(Items.IRON_AXE),
            new ItemStack(Items.IRON_SHOVEL),
            new ItemStack(Items.IRON_HELMET),
            new ItemStack(Items.IRON_CHESTPLATE),
            new ItemStack(Items.IRON_LEGGINGS),
            new ItemStack(Items.IRON_BOOTS),
            new ItemStack(Items.BOW),
            new ItemStack(Items.ARROW, 32),
            new ItemStack(Items.TORCH, 16),
            new ItemStack(Items.OAK_LOG, 16),
            new ItemStack(Items.COBBLESTONE, 32),
            new ItemStack(Items.OBSIDIAN, 4)
    };

    public static void register() {
        // --- Вход в игру ---
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (player.getWorld().getRegistryKey() == World.OVERWORLD) {
                Config config = Config.getInstance();
                int centerX = config.chunkX * 16 + 8;
                int centerZ = config.chunkZ * 16 + 8;
                int y = config.baseHeight + 1;
                if (player.getBlockPos().getY() < 100) {
                    if (player.getWorld() instanceof ServerWorld serverWorld) {
                        player.teleport(serverWorld, centerX, y, centerZ, 0, 0);
                    }
                }
            }
            updatePlayerScore(player);
        });

        // --- Основной тик ---
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            boolean enabled = server.getGameRules().getBoolean(RANDOM_CHUNK_ENABLED);
            if (!enabled) return;

            if (timeMultiplier != 1.0) {
                for (ServerWorld world : server.getWorlds()) {
                    if (world.getRegistryKey() == World.OVERWORLD) {
                        long currentTime = world.getTimeOfDay();
                        timeAccumulator += (timeMultiplier - 1.0);
                        long add = (long) Math.floor(timeAccumulator);
                        if (add != 0) {
                            world.setTimeOfDay(currentTime + add);
                            timeAccumulator -= add;
                        }
                    }
                }
            }

            DayCounterState state = DayCounterState.get(server);
            ServerWorld overworld = server.getWorld(World.OVERWORLD);

            if (overworld != null && !state.isInitialized()) {
                initLayers(overworld);
                state.setInitialized(true);
                state.setCurrentHeight(Config.getInstance().baseHeight - 3);
                Config config = Config.getInstance();
                int centerX = config.chunkX * 16 + 8;
                int centerZ = config.chunkZ * 16 + 8;
                int y = config.baseHeight + 1;
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (player.getWorld().getRegistryKey() == World.OVERWORLD) {
                        player.teleport(overworld, centerX, y, centerZ, 0, 0);
                    }
                }
                spawnPeacefulMobs(overworld, state, true);
                state.setLastDay(overworld.getTimeOfDay() / 24000);
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updatePlayerScore(player);
            }

            if (overworld != null) {
                long time = overworld.getTimeOfDay();
                long currentDay = time / 24000;
                long lastDay = state.getLastDay();

                if (currentDay > lastDay) {
                    int newDay = (int) currentDay;
                    state.setLastDay(currentDay);
                    state.incrementDays();

                    addRandomLayers(overworld);

                    if (newDay % 5 == 0) {
                        spawnPeacefulMobs(overworld, state, false);
                    }
                    if (newDay % 10 == 0) {
                        spawnChest(overworld);
                    }
                    if (newDay >= 100 && !state.isHundredDaysAchievementGranted()) {
                        state.setHundredDaysAchievementGranted(true);
                        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                            giveHundredDaysAdvancement(player);
                        }
                    }

                    server.getPlayerManager().broadcast(Text.literal("День " + newDay + ": добавлены новые слои!"), false);
                    hasSpawnedHostileThisNight = false;
                }

                long timeOfDay = time % 24000;
                int currentDayInt = (int) currentDay;
                if (timeOfDay > 13000 && timeOfDay < 23000 && !hasSpawnedHostileThisNight && currentDayInt % 3 == 0 && currentDayInt > 0) {
                    spawnHostileMobs(overworld);
                    hasSpawnedHostileThisNight = true;
                }
                if (timeOfDay > 23000 && hasSpawnedHostileThisNight) {
                    hasSpawnedHostileThisNight = false;
                }
            }
        });

        // --- Автоматический сбор дропа ---
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
            if (!world.getServer().getGameRules().getBoolean(RANDOM_CHUNK_ENABLED)) return;
            if (serverPlayer.isCreative()) return;

            if (state.isToolRequired()) {
                ItemStack heldStack = serverPlayer.getMainHandStack();
                if (heldStack.isEmpty() || !heldStack.isSuitableFor(state)) {
                    return;
                }
            }

            List<ItemStack> drops = state.getBlock().getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, serverPlayer, serverPlayer.getMainHandStack());
            if (drops.isEmpty()) return;

            world.getServer().execute(() -> {
                Box box = new Box(pos).expand(1.0);
                world.getEntitiesByClass(net.minecraft.entity.ItemEntity.class, box, item -> true).forEach(Entity::discard);
            });

            for (ItemStack stack : drops) {
                if (!serverPlayer.getInventory().insertStack(stack)) {
                    serverPlayer.dropItem(stack, false);
                }
            }
        });
    }

    public static void initLayers(ServerWorld world) {
        Config config = Config.getInstance();
        int chunkX = config.chunkX;
        int chunkZ = config.chunkZ;
        int baseY = config.baseHeight;

        // Слой 0: трава
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                BlockPos pos = new BlockPos(chunkX * 16 + x, baseY, chunkZ * 16 + z);
                world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
            }
        }
        // Слой 1: дерево
        Block logBlock = getRandomLogBlock();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                BlockPos pos = new BlockPos(chunkX * 16 + x, baseY - 1, chunkZ * 16 + z);
                world.setBlockState(pos, logBlock.getDefaultState(), 3);
            }
        }
        // Слой 2: камень/булыжник
        Block stoneBlock = RANDOM.nextBoolean() ? Blocks.STONE : Blocks.COBBLESTONE;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                BlockPos pos = new BlockPos(chunkX * 16 + x, baseY - 2, chunkZ * 16 + z);
                world.setBlockState(pos, stoneBlock.getDefaultState(), 3);
            }
        }

        // --- Кровать в центре ---
        int centerX = chunkX * 16 + 8;
        int centerZ = chunkZ * 16 + 8;
        int y = baseY + 1;
        Block bedBlock = getRandomBedBlock();
        Direction direction = Direction.SOUTH;
        BlockPos feetPos = new BlockPos(centerX, y, centerZ);
        BlockPos headPos = feetPos.offset(direction);

        if (world.isAir(feetPos) && world.isAir(headPos)) {
            world.setBlockState(feetPos, bedBlock.getDefaultState()
                    .with(HorizontalFacingBlock.FACING, direction)
                    .with(net.minecraft.block.BedBlock.PART, net.minecraft.block.enums.BedPart.FOOT), 3);
            world.setBlockState(headPos, bedBlock.getDefaultState()
                    .with(HorizontalFacingBlock.FACING, direction)
                    .with(net.minecraft.block.BedBlock.PART, net.minecraft.block.enums.BedPart.HEAD), 3);
        }
    }

    private static Block getRandomBedBlock() {
        Block[] beds = {
                Blocks.RED_BED, Blocks.BLUE_BED, Blocks.GREEN_BED, Blocks.YELLOW_BED,
                Blocks.BLACK_BED, Blocks.WHITE_BED, Blocks.ORANGE_BED, Blocks.MAGENTA_BED,
                Blocks.LIGHT_BLUE_BED, Blocks.PINK_BED, Blocks.LIME_BED, Blocks.GRAY_BED,
                Blocks.LIGHT_GRAY_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BROWN_BED
        };
        return beds[RANDOM.nextInt(beds.length)];
    }

    private static Block getRandomLogBlock() {
        Block[] logs = {
                Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG,
                Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG
        };
        return logs[RANDOM.nextInt(logs.length)];
    }

    private static void generateRandomLayer(ServerWorld world, int chunkX, int chunkZ, int y) {
        List<Block> allBlocks = getAvailableBlocks();
        if (allBlocks.isEmpty()) return;
        Block mainBlock = allBlocks.get(RANDOM.nextInt(allBlocks.size()));
        BlockState mainState = mainBlock.getDefaultState();

        int fluidCount = 0;
        int maxFluid = 2;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                BlockPos pos = new BlockPos(chunkX * 16 + x, y, chunkZ * 16 + z);
                if (fluidCount < maxFluid && RANDOM.nextInt(200) == 0) {
                    Block fluidBlock = RANDOM.nextBoolean() ? Blocks.WATER : Blocks.LAVA;
                    world.setBlockState(pos, fluidBlock.getDefaultState(), 3);
                    fluidCount++;
                } else {
                    world.setBlockState(pos, mainState, 3);
                }
            }
        }
    }

    private static void addRandomLayers(ServerWorld world) {
        Config config = Config.getInstance();
        int chunkX = config.chunkX;
        int chunkZ = config.chunkZ;
        int layers = config.layersPerDay;

        DayCounterState state = DayCounterState.get(world.getServer());
        int startY = state.getCurrentHeight() - 1;

        for (int layer = 0; layer < layers; layer++) {
            int y = startY - layer;
            if (y < world.getBottomY()) return;
            generateRandomLayer(world, chunkX, chunkZ, y);
            state.setCurrentHeight(y);
        }
        state.setCurrentHeight(startY - layers + 1);
    }

    private static void spawnPeacefulMobs(ServerWorld world, DayCounterState state, boolean isFirst) {
        Config config = Config.getInstance();
        int centerX = config.chunkX * 16 + 8;
        int centerZ = config.chunkZ * 16 + 8;
        int y = config.baseHeight + 1;

        int count = isFirst ? 6 : 4 + RANDOM.nextInt(4);
        for (int i = 0; i < count; i++) {
            EntityType<?> type = PEACEFUL_MOBS[RANDOM.nextInt(PEACEFUL_MOBS.length)];
            double x = centerX + (RANDOM.nextDouble() - 0.5) * 14;
            double z = centerZ + (RANDOM.nextDouble() - 0.5) * 14;
            BlockPos spawnPos = new BlockPos((int) x, y, (int) z);
            if (world.getBlockState(spawnPos.down()).isSolid()) {
                type.spawn(world, spawnPos, SpawnReason.NATURAL);
            }
        }
    }

    private static void spawnHostileMobs(ServerWorld world) {
        Config config = Config.getInstance();
        int centerX = config.chunkX * 16 + 8;
        int centerZ = config.chunkZ * 16 + 8;
        int y = config.baseHeight + 1;

        var difficulty = world.getDifficulty();
        if (difficulty == net.minecraft.world.Difficulty.PEACEFUL) return;

        int count;
        if (difficulty == net.minecraft.world.Difficulty.EASY) count = 1;
        else if (difficulty == net.minecraft.world.Difficulty.NORMAL) count = 1 + RANDOM.nextInt(2);
        else count = 2 + RANDOM.nextInt(2);

        for (int i = 0; i < count; i++) {
            EntityType<?> type = HOSTILE_MOBS[RANDOM.nextInt(HOSTILE_MOBS.length)];
            double x = centerX + (RANDOM.nextDouble() - 0.5) * 14;
            double z = centerZ + (RANDOM.nextDouble() - 0.5) * 14;
            BlockPos spawnPos = new BlockPos((int) x, y, (int) z);
            if (world.getBlockState(spawnPos.down()).isSolid() && world.isNight()) {
                type.spawn(world, spawnPos, SpawnReason.NATURAL);
            }
        }
    }

    private static void spawnChest(ServerWorld world) {
        Config config = Config.getInstance();
        int centerX = config.chunkX * 16 + 8;
        int centerZ = config.chunkZ * 16 + 8;
        int y = config.baseHeight + 1;

        int attempts = 0;
        BlockPos chestPos = null;
        while (attempts < 20) {
            int dx = RANDOM.nextInt(16);
            int dz = RANDOM.nextInt(16);
            int px = config.chunkX * 16 + dx;
            int pz = config.chunkZ * 16 + dz;
            int py = y;
            BlockPos pos = new BlockPos(px, py, pz);
            if (world.getBlockState(pos).isAir() && world.getBlockState(pos.down()).isSolid()) {
                chestPos = pos;
                break;
            }
            attempts++;
        }
        if (chestPos == null) return;

        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 3);

        var chestEntity = world.getBlockEntity(chestPos);
        if (chestEntity instanceof ChestBlockEntity chest) {
            int itemCount = 3 + RANDOM.nextInt(5);
            for (int i = 0; i < itemCount; i++) {
                ItemStack stack = CHEST_LOOT[RANDOM.nextInt(CHEST_LOOT.length)].copy();
                stack.setCount(1 + RANDOM.nextInt(stack.getMaxCount()));
                chest.setStack(RANDOM.nextInt(27), stack);
            }
            chest.markDirty();
        }
    }

    private static void giveHundredDaysAdvancement(ServerPlayerEntity player) {
        Identifier advId = Identifier.of(RandomChunkMod.MOD_ID, "hundred_days");
        AdvancementEntry entry = player.getServer().getAdvancementLoader().get(advId);
        if (entry != null) {
            AdvancementProgress progress = player.getAdvancementTracker().getProgress(entry);
            if (!progress.isDone()) {
                player.getAdvancementTracker().grantCriterion(entry, "hundred_days");
                player.sendMessage(Text.literal("🎉 Ты прожил 100 дней на чанке! Получено достижение!"), false);
            }
        } else {
            RandomChunkMod.LOGGER.warn("Достижение hundred_days не найдено!");
        }
    }

    private static List<Block> getAvailableBlocks() {
        Config config = Config.getInstance();
        List<Block> allBlocks = new ArrayList<>();
        if (config.useAllBlocks) {
            for (Block block : Registries.BLOCK) {
                if (block.getDefaultState().isAir()) continue;
                try {
                    if (block.getDefaultState().isIn(BlockTags.FLOWERS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.SIGNS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.WALL_SIGNS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.STANDING_SIGNS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.BANNERS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.BUTTONS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.PRESSURE_PLATES)) continue;
                    if (block.getDefaultState().isIn(BlockTags.RAILS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.SAPLINGS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.LEAVES)) continue;
                    if (block.getDefaultState().isIn(BlockTags.TALL_FLOWERS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.WOOL_CARPETS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.DOORS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.BEDS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.FLOWER_POTS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.FENCE_GATES)) continue;
                    if (block.getDefaultState().isIn(BlockTags.FENCES)) continue;
                    // Убираем кораллы
                    if (block.getDefaultState().isIn(BlockTags.CORAL_BLOCKS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.CORALS)) continue;
                    if (block.getDefaultState().isIn(BlockTags.WALL_CORALS)) continue;
                } catch (Exception ignored) {}
                allBlocks.add(block);
            }
        } else {
            for (String id : config.blockFilter) {
                Block block = Registries.BLOCK.get(Identifier.of(id));
                if (block != null) allBlocks.add(block);
            }
        }
        return allBlocks;
    }

    private static void updatePlayerScore(ServerPlayerEntity player) {
        DayCounterState state = DayCounterState.get(player.getServer());
        int days = state.getTotalDays();
        ServerWorld world = player.getServer().getWorld(World.OVERWORLD);
        float progress = 0f;
        if (world != null) {
            long time = world.getTimeOfDay();
            progress = (time % 24000) / 24000.0f;
        }
        int percent = (int) (progress * 100);
        String display = String.format("[%s] - День %d - %d%%",
                player.getName().getString(), days, percent);
        player.sendMessage(Text.literal(display), true);
    }

    public static void setTimeMultiplier(double multiplier) {
        timeMultiplier = Math.max(1.0, Math.min(100.0, multiplier));
        timeAccumulator = 0.0;
        RandomChunkMod.LOGGER.info("Скорость времени установлена: {}", timeMultiplier);
    }

    public static double getTimeMultiplier() {
        return timeMultiplier;
    }
}