package de.slevermann.fabric.dadjokes;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class DadJokeCommand implements Command<ServerCommandSource> {

    private final DadJokeFetcher dadJokeFetcher;

    public DadJokeCommand(DadJokeFetcher dadJokeFetcher) {
        this.dadJokeFetcher = dadJokeFetcher;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        dadJokeFetcher.getDadJoke(context.getSource().getServer()).thenAccept(lines -> {
            for (String s : lines) {
                context.getSource().getServer().getPlayerManager().broadcast(Text.of(s), false);
            }
        });
        return 1;
    }
}
