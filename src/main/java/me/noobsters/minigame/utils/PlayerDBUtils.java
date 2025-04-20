package me.noobsters.minigame.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;

import lombok.Getter;

/**
 * More info of the contents of the responses here https://playerdb.co
 */
public class PlayerDBUtils {
    private static HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build();
    private static Gson gson = new Gson();

    public static String getUser(String username) throws IOException, InterruptedException {
        var uri = URI.create("https://playerdb.co/api/player/minecraft/" + username);
        var request = HttpRequest.newBuilder(uri).header("accept", "application/json").build();

        var response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    public static String getUser(UUID uuid) throws IOException, InterruptedException {
        return getUser(uuid.toString());
    }

    public static JsonObject getPlayerObject(String nameOrID) {
        try {
            var request = getUser(nameOrID);
            var response = gson.fromJson(request, JsonObject.class);
            return response.getAsJsonObject("data").getAsJsonObject("player");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CompletableFuture<JsonObject> getPlayerObjectAsync(String nameOrID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getPlayerObject(nameOrID);
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * TODO: GenericPlayer
     */
    public static class GenericPlayer {
        private @Getter UUID uuid;
        private @Getter String playerName;

        public GenericPlayer(UUID uuid, String playerName) {
            this.uuid = uuid;
            this.playerName = playerName;
        }

        public static GenericPlayer of(UUID uuid, String playerName) {
            return new GenericPlayer(uuid, playerName);
        }

        public static GenericPlayer of(String uuid, String playerName) {
            return new GenericPlayer(UUID.fromString(uuid), playerName);
        }

        /**
         * Helper function that queries bukkit cached and playerdb.co for a player's
         * information.
         *
         * @param playerName Stringified playerName, caps shouldn't matter
         * @return {@link GenericPlayer} object containing player's name and uuid, or
         *         null if not existant.
         */
        public static GenericPlayer getGenericPlayer(String playerName) {
            var cachedPlayer = Bukkit.getOfflinePlayerIfCached(playerName);
            /** Null-safety check */
            if (cachedPlayer != null) {
                return GenericPlayer.of(cachedPlayer.getUniqueId(), cachedPlayer.getName());
            }
            var playerMinecraft = PlayerDBUtils.getPlayerObject(playerName);
            /** Null-safety check */
            if (playerMinecraft != null) {
                return GenericPlayer.of(playerMinecraft.get("id").getAsString(),
                        playerMinecraft.get("username").getAsString());
            }
            /** If we get to this point, return null */
            return null;

        }

    }
}
