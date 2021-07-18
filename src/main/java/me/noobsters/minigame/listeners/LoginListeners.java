package me.noobsters.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class LoginListeners implements Listener {
    private @NonNull UHC instance;
            
    private static String FULL_MESSAGE = ChatColor.translateAlternateColorCodes('&',
            "&fY los billetes?! \n &aUpgrade your rank at &6noobsters.buycraft.net");
    //private static String NOT_IN_WL = ChatColor.WHITE + "You are not in whitelist.\n" + ChatColor.RED + "Request whitelist to the host.";
    //private static String NOT_PLAYING = ChatColor.WHITE + "You are not in whitelist.\n" + Game.getUpToMVP();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void slotLimit(PlayerLoginEvent e) {
        final var player = e.getPlayer();
        
        if (!shouldLogin(player)){
            e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, FULL_MESSAGE);
            return;
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void slotLimit(PlayerJoinEvent e) {
        final var player = e.getPlayer();

        if (!shouldLogin(player)){
            player.kickPlayer(FULL_MESSAGE);
            return;
        }

    }

    private boolean shouldLogin(Player player) {
        final var uuid = player.getUniqueId().toString();
        final var online = Bukkit.getOnlinePlayers().size();
        final var maxSlots = instance.getGame().getUhcslots();
        
        var game = instance.getGame();

        /*Bukkit.broadcastMessage("perm " + player.hasPermission("reserved.slot") + " " + player.hasPermission("host.perm"));
        Bukkit.broadcastMessage("private? " + !instance.getGame().isPrivateGame() + " " + player.hasPermission("uhc.spec.ingame"));
        Bukkit.broadcastMessage("online " + (online < maxSlots) + " " +  instance.getGame().getWhitelist().containsValue(uuid));*/

        if(((!player.hasPermission("host.perm") || !player.hasPermission("reserved.slot")) && online >= maxSlots) 
            || (game.isPrivateGame() && !player.hasPermission("uhc.spec.ingame")) 
                || (game.isWhitelistEnabled() && !game.getWhitelist().containsValue(uuid) && !player.hasPermission("host.perm"))){

                return false;
        }

        return true;

    }



}