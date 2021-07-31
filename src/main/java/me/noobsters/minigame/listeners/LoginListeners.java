package me.noobsters.minigame.listeners;

import org.bukkit.Bukkit;
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
            "&fServer full! \n &aUpgrade your rank at &6noobsters.buycraft.net");
    
    private static String PRIVATE_NO_WL = ChatColor.translateAlternateColorCodes('&',
            "&fPrivate Game! You need whitelist to enter.");
    
    private static String NO_SPEC_RANK = ChatColor.translateAlternateColorCodes('&',
            "&fY los billetes?! You rank doesn't have spectator mode. \n &aUpgrade your rank at &6noobsters.buycraft.net");
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void slotLimit(PlayerLoginEvent e) {
        final var player = e.getPlayer();
        
        final var uuid = player.getUniqueId().toString();
        final var online = Bukkit.getOnlinePlayers().size();
        final var maxSlots = instance.getGame().getUhcslots();
        
        var game = instance.getGame();
        
        if(player.hasPermission("host.perm")) return;

        if(online >= maxSlots && !player.hasPermission("reserved.slot")){

            //MSG SERVER IS FULL
            e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, FULL_MESSAGE);
            return;
        }
        
        if(game.isPrivateGame()){
            
            if(game.isWhitelistEnabled()){
                if(game.getWhitelist().containsValue(uuid)){
                    return;

                }else{
                    //MSG GAME PRIVADO NO ESTAS EN WL
                    e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, PRIVATE_NO_WL);
                    return;
                }

            }else{
                return;
            }
        

        }else{

            if(game.isWhitelistEnabled()){
                if(game.getWhitelist().containsValue(uuid)){
                    return;

                }else {
                    if(player.hasPermission("uhc.spec.ingame")){
                        
                        return;
                    }else{
                        //MSG NO TIENES RANK PARA SPEC
                        e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, NO_SPEC_RANK);
                        return;
                    }

                }

            }else{

                return;
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void slotLimit(PlayerJoinEvent e) {
        final var player = e.getPlayer();

        final var uuid = player.getUniqueId().toString();
        final var online = Bukkit.getOnlinePlayers().size();
        final var maxSlots = instance.getGame().getUhcslots();
        
        var game = instance.getGame();
        
        if(player.hasPermission("host.perm")) return;

        if(online >= maxSlots && !player.hasPermission("reserved.slot")){

            //MSG SERVER IS FULL
            player.kickPlayer(FULL_MESSAGE);
            return;
        }
        
        if(game.isPrivateGame()){
            
            if(game.isWhitelistEnabled()){
                if(game.getWhitelist().containsValue(uuid)){
                    return;

                }else{
                    //MSG GAME PRIVADO NO ESTAS EN WL
                    player.kickPlayer(PRIVATE_NO_WL);
                    return;
                }

            }else{
                return;
            }
        

        }else{

            if(game.isWhitelistEnabled()){
                if(game.getWhitelist().containsValue(uuid)){
                    return;

                }else {
                    if(player.hasPermission("uhc.spec.ingame")){
                        
                        return;
                    }else{
                        //MSG NO TIENES RANK PARA SPEC
                        player.kickPlayer(NO_SPEC_RANK);
                        return;
                    }

                }

            }else{

                return;
            }

        }

    }


}