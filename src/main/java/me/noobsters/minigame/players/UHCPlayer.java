package me.noobsters.minigame.players;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.players.serializers.ItemStackSerializers;
import me.noobsters.minigame.teams.objects.Team;

@RequiredArgsConstructor
public @Data class UHCPlayer {
    @SuppressWarnings("java:S116")
    private final @Getter UUID UUID;
    private boolean thanksHost = false;
    private boolean specInfo = false;
    private boolean alive = false;
    private boolean dead = false;
    private double lastKnownHealth = 20.0;
    private PositionObject lastKnownPosition;
    private ItemStack[] lastKnownInventory;
    private static Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(ItemStack.class, ItemStackSerializers.getItemStackSerializer())
            .registerTypeAdapter(ItemStack[].class, ItemStackSerializers.getItemStackArraySerializer()).create();
    private static Gson gsonNoInv = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(ItemStack[].class, ItemStackSerializers.getItemStackSerializerNoItems()).create();

    //Stats
    private int kills = 0;
    private int projectileShoots = 0;
    private int projectileHits = 0;
    private int minedDiamonds = 0;
    private int minedGold = 0;
    private int minedAncientDebris = 0;
    private int hostileMobs = 0;
    private int peacefulMobs = 0;
    private int goldenHeads = 0;
    private int notchApples = 0;
    private int goldenApples = 0;

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public String toStringNoInventory() {
        return gsonNoInv.toJson(this);
    }

    public Team getTeam(UHC instance){
        return instance.getTeamManger().getPlayerTeam(UUID);
    }
    public void setLastKnownPositionFromLoc(Location loc) {
        setLastKnownPosition(PositionObject.getPositionFromWorld(loc));
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(UUID);
    }

}