package me.infinityz.minigame.players;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.UUID;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("all")
@RequiredArgsConstructor
public class UHCPlayer {
    private final @Getter UUID UUID;
    private @NonNull @Getter @Setter int kills;
    private @NonNull @Getter @Setter boolean alive;
    private @Getter @Setter boolean dead = false;
    private @Getter @Setter double lastKnownHealth = 20.0;
    private @Getter @Setter PositionObject lastKnownPosition;
    private @Getter @Setter ItemStack[] lastKnownInventory;

    @Override
    public String toString() {
        var gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeAdapter(ItemStack[].class, new ItemStackArrayAdapter()).create();
        return gson.toJson(this);
    }

    public String toStringNoInventory() {
        var gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(ItemStack[].class, new JsonSerializer<ItemStack[]>() {

                    @Override
                    public JsonElement serialize(ItemStack[] src, Type srcType, JsonSerializationContext context) {
                        var json = new JsonObject();
                        json.addProperty("items",
                                Arrays.stream(src).filter(it -> it != null && it.getType() != Material.AIR).count());
                        return json;
                    }

                }).create();
        return gson.toJson(this);
    }

    public void setLastKnownPositionFromLoc(Location loc) {
        setLastKnownPosition(PositionObject.getPositionFromWorld(loc));
    }

}