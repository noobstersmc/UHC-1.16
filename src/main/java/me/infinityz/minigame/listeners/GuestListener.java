package me.infinityz.minigame.listeners;

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

        if (player.hasPermission("group.vandal") && !player.hasPermission("group.vandalhost")
                && player.getName().equalsIgnoreCase(instance.getGame().getHostname())) {
            player.addAttachment(instance).setPermission("group.vandalhost", true);
            player.updateCommands();
        }

    }


}
