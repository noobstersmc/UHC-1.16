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
import me.noobsters.minigame.game.Game;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class LoginListeners implements Listener {
    private @NonNull UHC instance;
            
    private static String FULL_MESSAGE = ChatColor.translateAlternateColorCodes('&',
    "&fServer is full! \n &aUpgrade your rank at &6noobsters.buycraft.net");
    private static String NOT_IN_WL = ChatColor.WHITE + "You are not in whitelist.\n" + ChatColor.RED + "Request whitelist to the host.";
    private static String NOT_PLAYING = ChatColor.WHITE + "You are not in whitelist.\n" + Game.getUpToMVP();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void slotLimit(PlayerLoginEvent e) {
        var game = instance.getGame();
        final var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        if (!shoudLogin(player)){
            e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, FULL_MESSAGE);
            return;

        }else if(game.isWhitelistEnabled()){
            if(player.hasPermission("host.perm") || (!game.isPrivateGame() && player.hasPermission("uhc.spec.ingame"))) return;

            if(!game.getWhitelist().containsValue(uuid)){
                //must be kicked out
                if(game.isPrivateGame()){
                    e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_WHITELIST, NOT_IN_WL);
                }else{
                    e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_WHITELIST, NOT_PLAYING);
                }
            }

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void slotLimit(PlayerJoinEvent e) {
        final var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();
        if (!shoudLogin(player)){
            player.kickPlayer(FULL_MESSAGE);
            return;

        }else if(game.isWhitelistEnabled()){
            if(player.hasPermission("host.perm") || (!game.isPrivateGame() && player.hasPermission("uhc.spec.ingame"))) return;

            if(!game.getWhitelist().containsValue(uuid)){
                //must be kicked out
                if(game.isPrivateGame()){
                    player.kickPlayer(NOT_IN_WL);
                }else{
                    player.kickPlayer(NOT_PLAYING);
                }
            }

        }

    }

    private boolean shoudLogin(final Player player) {
        final var uuid = player.getUniqueId().toString();
        final var online = Bukkit.getOnlinePlayers().size();
        final var maxSlots = instance.getGame().getUhcslots();

        return online <= maxSlots || player.hasPermission("reserved.slot") || instance.getGame().getWhitelist().containsValue(uuid);
    }


}