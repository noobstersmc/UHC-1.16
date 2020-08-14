package me.infinityz.minigame.teams.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.teams.events.PlayerJoinedTeamEvent;
import me.infinityz.minigame.teams.events.PlayerLeftTeamEvent;

@RequiredArgsConstructor
public class TeamListeners implements Listener {
    private @NonNull UHC instance;

    @EventHandler
    public void onJoinTeam(PlayerJoinedTeamEvent e) {

    }

    @EventHandler
    public void onLeftTeam(PlayerLeftTeamEvent e) {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

    }

}