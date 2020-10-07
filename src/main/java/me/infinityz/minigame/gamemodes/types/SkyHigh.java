package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class SkyHigh extends IGamemode implements Listener {
    private boolean damage = false;
    private BukkitTask task;
    private UHC instance;

    public SkyHigh(UHC instance) {
        super("SkyHigh", "Players that stay below Y=150 periodically recieve damage after border time.");
        this.instance = instance;
        instance.getCommandManager().registerCommand(new SkyHighCMD());
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;

        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;

        setEnabled(false);
        return true;
    }

    /**
     * InnerGoToHell
     */
    @CommandPermission("uhc.scenarios")
    @CommandAlias("skyhigh")
    public class SkyHighCMD extends BaseCommand {

        @Default
        public void toggleDamage(CommandSender sender) {
            damage = !damage;
            sender.sendMessage("Damage has been switch to: " + damage);

        }

        @Subcommand("start")
        public void startDamageTask(CommandSender sender,  @Default("600")Integer interval, @Default("600") Integer delay){
            if(task == null || task.isCancelled()){
                task = new SkyHighDamageTask().runTaskTimerAsynchronously(instance, delay, interval);
                damage = true;
                sender.sendMessage("Starting the damage task with delay " + delay + " and interval of " + interval);
                Bukkit.broadcastMessage(ChatColor.of("#7fe5f0") + "Go above coordinate Y=150 now. Player's that remain below Y=150 will take 1 heart of damage every " + (delay/20) + " seconds.");
            }else{

                sender.sendMessage("task is already running, cancel it first.");
            }

        }

        @Subcommand("stop")
        public void stopDamageTask(CommandSender sender){
            if(task != null ){
                task.cancel();
                sender.sendMessage("Canceling the task");
            }
            
        }

    }

    /**
     * InnerGoToHell
     */
    public class SkyHighDamageTask extends BukkitRunnable {

        @Override
        public void run() {
            if(isCancelled())
                return;
            
            if (damage)
                for (var players : Bukkit.getOnlinePlayers())
                    if (players.getGameMode() == GameMode.SURVIVAL && players.getLocation().getBlock().getY() >150)
                        players.setHealth(players.getHealth()-2);

        }

    }

}
