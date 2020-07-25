package me.infinityz.minigame.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.tasks.ScatterTask;

@CommandPermission("staff.perm")
@CommandAlias("uhc|game")
public class UHCCommand extends BaseCommand {

    UHC instance;

    public UHCCommand(UHC instance) {
        this.instance = instance;
    }
 
    @Conditions("ingame")
    @Subcommand("respawn|revive|reinstantiate")
    @CommandCompletion("@players")
    @Syntax("<target> &e- Player that has to be scattered")
    public void onCommand(CommandSender sender, OnlinePlayer target) {
        if(target == null)return;
        UHCPlayer uhcp =  instance.getPlayerManager().getPlayer(target.player.getUniqueId());

        if(uhcp == null){
            //Create the player instance if not existant
            uhcp = instance.getPlayerManager().addCreateUHCPlayer(target.player.getUniqueId(), true);
            uhcp.setSpectator(false);
            
        }else{
            if(uhcp.isAlive()){
                sender.sendMessage("Player is still alive.");
                return;
            }
            uhcp.setAlive(true);
            uhcp.setSpectator(false);
        }

        sender.sendMessage("The player has been scattered into the world");

        target.player.teleport(ScatterTask.findScatterLocation(Bukkit.getWorlds().get(0), (int)Bukkit.getWorlds().get(0).getWorldBorder().getSize()/2));
        target.player.setGameMode(GameMode.SURVIVAL);
    }

    @Conditions("ingame")
    @Subcommand("alive")
    public void alive(CommandSender sender){
        sender.sendMessage("Alive offline players: ");
        instance.getPlayerManager().getUhcPlayerMap().entrySet().forEach(entry->{
            if(entry.getValue().isAlive()){
                OfflinePlayer of = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
                if(!of.isOnline()){
                    sender.sendMessage(" - " + of.getName());
                }
            }
        });
    }

}