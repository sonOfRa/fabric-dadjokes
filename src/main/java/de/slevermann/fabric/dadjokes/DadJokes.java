package de.slevermann.fabric.dadjokes;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.time.Duration;

public class DadJokes implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        final DadJokeFetcher dadJokeFetcher = new DadJokeFetcher();
        final DadJokeCommand dadJokeCommand = new DadJokeCommand(dadJokeFetcher);
        final LiteralCommandNode<ServerCommandSource> dadJokeNode = CommandManager
                .literal("dadjoke")
                .executes(dadJokeCommand).build();
        final LiteralCommandNode<ServerCommandSource> flachWitzNode = CommandManager
                .literal("flachwitz")
                .executes(dadJokeCommand).build();
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            dispatcher.getRoot().addChild(dadJokeNode);
            dispatcher.getRoot().addChild(flachWitzNode);
        }));

        final EndTickListener endTickListener = new EndTickListener(dadJokeFetcher, Duration.ofMinutes(10));
        ServerTickEvents.END_SERVER_TICK.register(endTickListener);
    }
}
