package me.noobsters.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.chunks.ChunksManager;
import me.noobsters.minigame.events.PlayerJoinedLateEvent;
import me.noobsters.minigame.game.Game;
import me.noobsters.minigame.gamemodes.types.UHCMeetup;
import me.noobsters.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("latescatter|ls|play")
public class LatescatterCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Default
    @Conditions("ingame")
    public void lateScatter(@Conditions("hasNotDied|spec") UHCPlayer uhcPlayer) {
        var player = uhcPlayer.getPlayer();
        //UHC MEETUP ONLY FOR VIPS BEFORE BORDER

        if (instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class)){
            if (!player.hasPermission("latescatter.meetup")){
                player.sendMessage(Game.getUpToVIP());
                return;
            }else if(instance.getGame().getGameTime() >= instance.getGame().getBorderTime()){
                player.sendMessage(ChatColor.RED + "LateScatter is only available before border start to move.");
                return;
            }
            
        }

        //LATESCATTER ONLY AVAILABLE IN NO PVP TIME
        else if(instance.getGame().getGameTime() >= instance.getGame().getPvpTime()){
            player.sendMessage(ChatColor.RED + 
            "LateScatter time is up. (Max time " + instance.getGame().getPvpTime()/60 + " minutes in)");
            return;
            
        }

        var world = Bukkit.getWorld("world");
        var worldBorderSizeHaved = (int) world.getWorldBorder().getSize() / 2;

        player.sendMessage((ChatColor.of("#7ab83c") + "Loading a location..."));
        player.setGameMode(GameMode.SURVIVAL);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        
        var loc = ChunksManager.findScatterLocation(world, worldBorderSizeHaved).add(0, 5, 0);
        player.teleport(loc);
        player.sendMessage(ChatColor.of("#7ab83c") + "You have been scattered into the world.");
        uhcPlayer.setAlive(true);
        Bukkit.getPluginManager().callEvent(PlayerJoinedLateEvent.of(player));
        
    }

}