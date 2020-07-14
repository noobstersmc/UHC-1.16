package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import me.infinityz.minigame.UHC;

public class ListenerManager {
    UHC instance;
    LobbyListeners lobby;
    ScatterListeners scatter;
    IngameListeners ingameListeners;

    public ListenerManager(UHC instance) {
        this.instance = instance;
        lobby = new LobbyListeners(instance);
        scatter = new ScatterListeners(instance);
        ingameListeners = new IngameListeners(instance);

        Bukkit.getPluginManager().registerEvents(new GlobalListener(instance), instance);
        Bukkit.getPluginManager().registerEvents(lobby, instance);
    
    }

    public void unregisterListener(Listener listener){
        HandlerList.unregisterAll(listener);
    }
    
    public void registerListener(Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

    public IngameListeners getIngameListeners() {
        return ingameListeners;
    }

    public LobbyListeners getLobby() {
        return lobby;
    }
    public ScatterListeners getScatter() {
        return scatter;
    }

}