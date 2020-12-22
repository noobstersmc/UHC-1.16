package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.infinityz.minigame.UHC;

public class GuestListener implements Listener {

    private UHC instance;

    public GuestListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void joinRank(PlayerJoinEvent e) {
        var player = e.getPlayer();

        if (player.hasPermission("group.vandal") && !player.hasPermission("group.vandalhost") && !player.hasPermission("group.admin")
                && player.getName().equalsIgnoreCase(instance.getGame().getHostname())) {
            if (!player.hasPlayedBefore()) Bukkit.dispatchCommand(e.getPlayer(), "scenario UHC Vandálico");
            player.addAttachment(instance).setPermission("group.vandalhost", true);
            player.updateCommands();
        }

    }


}
