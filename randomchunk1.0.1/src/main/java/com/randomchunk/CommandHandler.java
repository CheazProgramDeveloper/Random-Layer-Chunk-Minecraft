package com.randomchunk;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class CommandHandler {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("randomchunk")
                .then(literal("reload")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            Config.load();
                            context.getSource().sendMessage(Text.literal("Конфиг перезагружен"));
                            return 1;
                        })
                )
                .then(literal("reset")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            DayCounterState state = DayCounterState.get(context.getSource().getServer());
                            state.setCurrentHeight(Config.getInstance().baseHeight);
                            state.setInitialized(false);
                            state.setLastDay(0);
                            context.getSource().sendMessage(Text.literal("Состояние сброшено, чанк пересоздастся при следующем тике"));
                            return 1;
                        })
                )
                .then(literal("init")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            ServerWorld overworld = context.getSource().getServer().getWorld(World.OVERWORLD);
                            if (overworld == null) {
                                context.getSource().sendMessage(Text.literal("Мир Overworld не найден!"));
                                return 0;
                            }
                            DayCounterState state = DayCounterState.get(context.getSource().getServer());
                            if (!state.isInitialized()) {
                                DayCounterEvents.initLayers(overworld);
                                state.setInitialized(true);
                                state.setCurrentHeight(Config.getInstance().baseHeight - 3);
                                state.setLastDay(overworld.getTimeOfDay() / 24000);
                                context.getSource().sendMessage(Text.literal("Чанк принудительно создан!"));
                                var player = context.getSource().getPlayer();
                                if (player != null) {
                                    Config config = Config.getInstance();
                                    int centerX = config.chunkX * 16 + 8;
                                    int centerZ = config.chunkZ * 16 + 8;
                                    int y = config.baseHeight + 1;
                                    player.teleport(overworld, centerX, y, centerZ, 0, 0);
                                    player.sendMessage(Text.literal("Телепортирован на чанк!"), false);
                                }
                            } else {
                                context.getSource().sendMessage(Text.literal("Чанк уже инициализирован."));
                            }
                            return 1;
                        })
                )
                .then(literal("setheight")
                        .then(argument("height", IntegerArgumentType.integer(100, 3000))
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    int newHeight = IntegerArgumentType.getInteger(context, "height");
                                    Config config = Config.getInstance();
                                    config.worldHeight = newHeight;
                                    Config.save();
                                    context.getSource().sendMessage(Text.literal("Высота мира установлена: " + newHeight + ". Перезапустите мир для применения."));
                                    return 1;
                                })
                        )
                )
        );

        dispatcher.register(literal("speedtime")
                .then(literal("set")
                        .then(argument("multiplier", IntegerArgumentType.integer(1, 100))
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    int value = IntegerArgumentType.getInteger(context, "multiplier");
                                    DayCounterEvents.setTimeMultiplier(value);
                                    context.getSource().sendMessage(Text.literal("Скорость времени установлена: " + value + "x"));
                                    return 1;
                                })
                        )
                )
        );
    }
}