package me.infinityz.minigame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.infinityz.minigame.UHC;

public class GlobalListener implements Listener {
    UHC instance;

    public GlobalListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        switch (instance.gameStage) {
            case INGAME:
            case LOBBY:
            case SCATTER: {

                break;
            }
            default:
                break;
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (instance.getScoreboardManager().getFastboardMap().containsKey(e.getPlayer().getUniqueId().toString())) {

        }

    }

}