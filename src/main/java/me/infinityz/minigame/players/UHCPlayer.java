package me.infinityz.minigame.players;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.infinityz.minigame.players.serializers.ItemStackSerializers;

@RequiredArgsConstructor
public class UHCPlayer {
    @SuppressWarnings("java:S116")
    private final @Getter UUID UUID;
    private @Getter @Setter int kills = 0;
    private @Getter @Setter int minedDiamonds = 0;
    private @Getter @Setter boolean alive = false;
    private @Getter @Setter boolean dead = false;
    private @Getter @Setter double lastKnownHealth = 20.0;
    private @Getter @Setter PositionObject lastKnownPosition;
    private @Getter @Setter ItemStack[] lastKnownInventory;
    private static Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(ItemStack.class, ItemStackSerializers.getItemStackSerializer())
            .registerTypeAdapter(ItemStack[].class, ItemStackSerializers.getItemStackArraySerializer()).create();
    private static Gson gsonNoInv = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(ItemStack[].class, ItemStackSerializers.getItemStackSerializerNoItems()).create();

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public String toStringNoInventory() {
        return gsonNoInv.toJson(this);
    }

    public void setLastKnownPositionFromLoc(Location loc) {
        setLastKnownPosition(PositionObject.getPositionFromWorld(loc));
    }

}