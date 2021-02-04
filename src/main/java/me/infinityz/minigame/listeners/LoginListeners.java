package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class LoginListeners implements Listener {
    private @NonNull UHC instance;
            
    private static String FULL_MESSAGE = ChatColor.translateAlternateColorCodes('&',
    "&fServer is full! \n &aUpgrade your rank at &6noobsters.buycraft.net");
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void slotLimit(PlayerLoginEvent e) {
        final var player = e.getPlayer();
        if (!shoudLogin(player))
            e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, FULL_MESSAGE);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void slotLimit(PlayerJoinEvent e) {
        final var player = e.getPlayer();
        if (!shoudLogin(player))
            player.kickPlayer(FULL_MESSAGE);

    }

    private boolean shoudLogin(final Player player) {
        final var online = Bukkit.getOnlinePlayers().size();
        final var maxSlots = instance.getGame().getUhcslots();

        return online <= maxSlots || player.hasPermission("reserved.slot");
    }


}