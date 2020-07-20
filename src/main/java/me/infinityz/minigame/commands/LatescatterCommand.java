package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.tasks.ScatterTask;

@CommandPermission("uhc.admin")
@CommandAlias("latescatter")
public class LatescatterCommand extends BaseCommand {

    UHC instance;

    public LatescatterCommand(UHC instance) {
        this.instance = instance;
    }

    @Default
    @Syntax("<player> &e- Player that has to be scattered")
    public void onCommand(CommandSender sender, String tg) {
        if(tg.isEmpty()){
            return;
        }
        Player target = Bukkit.getPlayer(tg);
        if(target == null)return;
        UHCPlayer uhcp =  instance.getPlayerManager().getPlayer(target.getUniqueId());

        if(uhcp == null){
            //Create the player instance if not existant
            instance.getPlayerManager().addCreateUHCPlayer(target.getUniqueId());
            uhcp =  instance.getPlayerManager().getPlayer(target.getUniqueId());
            uhcp.setAlive(true);
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

        target.teleport(ScatterTask.findScatterLocation(Bukkit.getWorlds().get(0), (int)Bukkit.getWorlds().get(0).getWorldBorder().getSize()/2));
        target.setGameMode(GameMode.SURVIVAL);
    }

}