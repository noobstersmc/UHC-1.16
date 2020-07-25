package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.listeners.GlobalListener;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.tasks.ScatterTask;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("vip.perm")
@CommandAlias("latescatter|ls|play")
public class LatescatterCMD extends BaseCommand {

    UHC instance;

    public LatescatterCMD(UHC instance) {
        this.instance = instance;
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "alive",
                (context, executionContext, player) -> {
                    UHCPlayer up = instance.getPlayerManager().getPlayer(player.getUniqueId());
                    if (up == null)
                        throw new ConditionFailedException("You must be a player to do this");
                    if (!up.isAlive())
                        throw new ConditionFailedException("You must be alive to do this");
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "spec",
                (context, executionContext, player) -> {
                    UHCPlayer up = instance.getPlayerManager().getPlayer(player.getUniqueId());
                    if (up == null)
                        throw new ConditionFailedException("You must be an spectator to do this");
                    if (!up.isSpectator())
                        throw new ConditionFailedException("You must be spectator to do this");
                });

        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "dead",
                (context, executionContext, player) -> {
                    UHCPlayer up = instance.getPlayerManager().getPlayer(player.getUniqueId());
                    if (up == null)
                        throw new ConditionFailedException(ChatColor.RED + "You must be a player to do this");
                    if (up.isAlive())
                        throw new ConditionFailedException(ChatColor.RED + "You must not be alive to do this");
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "hasdied",
                (context, executionContext, player) -> {
                    UHCPlayer up = instance.getPlayerManager().getPlayer(player.getUniqueId());
                    if (up == null)
                        throw new ConditionFailedException((ChatColor.RED + "You must be a player to do this"));
                    if (up.hasDied)
                        throw new ConditionFailedException((ChatColor.RED + "You must not have died to do this."));
                });

        instance.getCommandManager().getCommandConditions().addCondition("ingame", (context) -> {
            if (!instance.gameStage.equals(Stage.INGAME))
                throw new ConditionFailedException((ChatColor.RED + "Game must be playing!"));
        });

        instance.getCommandManager().getCommandConditions().addCondition("time", (c) -> {
            if (c.hasConfig("min") && c.getConfigValue("min", 0) > GlobalListener.time) {
                throw new ConditionFailedException((ChatColor.RED + "Min value must be " + c.getConfigValue("min", 0)));
            }
            if (c.hasConfig("max") && c.getConfigValue("max", 3) < GlobalListener.time) {
                throw new ConditionFailedException((ChatColor.RED + "Max value must be " + c.getConfigValue("max", 3)));
            }
        });
    }

    @Default
    @Conditions("ingame|time:max=1200")
    public void lateScatter(@Conditions("hasdied|spec") Player player) {
        UHCPlayer uhcp =  instance.getPlayerManager().getPlayer(player.getUniqueId());

        if(uhcp == null){
            //Create the player instance if not existant
            uhcp = instance.getPlayerManager().addCreateUHCPlayer(player.getUniqueId(), true);
            uhcp.setSpectator(false);
            
        }else{
            if(uhcp.isAlive()){
                player.sendMessage("Player is still alive.");
                return;
            }
            uhcp.setAlive(true);
            uhcp.setSpectator(false);
        }

        player.sendMessage((ChatColor.YELLOW + "The player has been scattered into the world."));

        player.teleport(ScatterTask.findScatterLocation(Bukkit.getWorlds().get(0), (int)Bukkit.getWorlds().get(0).getWorldBorder().getSize()/2));
        player.setGameMode(GameMode.SURVIVAL);
    }

}