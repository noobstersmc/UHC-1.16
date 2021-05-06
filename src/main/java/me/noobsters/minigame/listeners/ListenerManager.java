package me.noobsters.minigame.listeners;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.Getter;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.teams.listeners.TeamListeners;

public class ListenerManager {
    private UHC instance;
    private @Getter LobbyListeners lobby;
    private @Getter ScatterListeners scatter;
    private @Getter IngameListeners ingameListeners;
    private @Getter GracePeriodListeners gracePeriodListeners;

    public ListenerManager(UHC instance) {
        this.instance = instance;
        lobby = new LobbyListeners(instance);
        scatter = new ScatterListeners(instance);
        ingameListeners = new IngameListeners(instance);
        gracePeriodListeners = new GracePeriodListeners(instance);
    
        registerListener(new GlobalListener(instance));
        registerListener(new ConfigListener(instance));
        registerListener(new ConfigListener(instance));
        registerListener(new TeamListeners(instance));
        registerListener(new StatsListener(instance));
        registerListener(new SpectatorListener(instance));

        registerListener(lobby);
        registerListener(gracePeriodListeners);

    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, instance);
        if (listener instanceof SpectatorListener) {
            /*
             * Hide all spectators to not spectators.
             */
            final var notSpecs = Bukkit.getOnlinePlayers().stream()
                    .filter(all -> all.getGameMode() != GameMode.SPECTATOR).collect(Collectors.toList());

            Bukkit.getOnlinePlayers().stream().filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                    .forEach(spectator -> notSpecs.forEach(all -> all.hidePlayer(instance, spectator)));

        }
    }

}