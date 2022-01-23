package de.slevermann.fabric.dadjokes;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static net.minecraft.util.Util.NIL_UUID;

public class EndTickListener implements ServerTickEvents.EndTick {

    private final AtomicLong counter;

    private final DadJokeFetcher fetcher;

    private final long tickInterval;

    public EndTickListener(DadJokeFetcher fetcher, Duration interval) {
        this.fetcher = fetcher;
        this.tickInterval = toTicks(interval);
        // Start the counter at 80% so the first joke happens a bit earlier into the cycle
        this.counter = new AtomicLong((tickInterval / 5) * 4);
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        if (counter.getAndUpdate(l -> l == tickInterval ? 0 : (l + 1)) == tickInterval) {
            if (server.getCurrentPlayerCount() > 0) {
                fetcher.getDadJoke(server).thenAccept(lines -> {
                    for (String s : lines) {
                        server.getPlayerManager().broadcast(Text.of(s), MessageType.CHAT, NIL_UUID);
                    }
                });
            }
        }
    }

    private long toTicks(Duration duration) {
        return duration.getSeconds() * 20;
    }
}
