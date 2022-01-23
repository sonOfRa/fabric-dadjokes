package de.slevermann.fabric.dadjokes;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DadJokeFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DadJokeFetcher.class);

    public List<String> fetchDadJoke(MinecraftServer server) {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URL("https://icanhazdadjoke.com/").openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Accept", "text/plain");
            connection.addRequestProperty("User-Agent", "Spigot Dad Joke Plugin " +
                    "(https://github.com/sonOfRa/fabric-dadjokes) @ "
                    + server.getServerIp());
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Fetching dad joke failed with HTTP %d %s"
                        .formatted(responseCode, connection.getResponseMessage()));
            }
            try (InputStream is = connection.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                String dadJoke = baos.toString(StandardCharsets.UTF_8);
                return Arrays.asList(dadJoke.split("\\R"));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to fetch dad joke", e);
            return Collections.emptyList();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public CompletableFuture<List<String>> getDadJoke(MinecraftServer server) {
        return CompletableFuture.supplyAsync(() -> fetchDadJoke(server));
    }
}
