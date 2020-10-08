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

public class GoToHell extends IGamemode implements Listener {
    private boolean damage = true;
    private BukkitTask task;
    private UHC instance;

    public GoToHell(UHC instance) {
        super("Go to Hell", "Players that stay in the overworld will periodically recieve damage after border time.");
        this.instance = instance;
        instance.getCommandManager().registerCommand(new GoToHellCMD());
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
    @CommandAlias("gotohell")
    public class GoToHellCMD extends BaseCommand {

        @Default
        public void toggleDamage(CommandSender sender) {
            damage = !damage;
            sender.sendMessage("Damage has been switch to: " + damage);

        }

        @Subcommand("start")
        public void startDamageTask(CommandSender sender,  @Default("100")Integer interval, @Default("100") Integer delay){
            if(task == null || task.isCancelled()){
                task = new GoToHellDamageTask().runTaskTimerAsynchronously(instance, delay, interval);
                damage = true;
                sender.sendMessage("Starting the damage task with delay " + delay + " and interval of " + interval);
                Bukkit.broadcastMessage(ChatColor.of("#cd4619") + "Go to the Nether now. Player's that remain in the overworld will take a heart of damage every " + (delay/20) + " seconds.");
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
    public class GoToHellDamageTask extends BukkitRunnable {

        @Override
        public void run() {
            if(isCancelled())
                return;
            
            if (damage)
                for (var players : Bukkit.getOnlinePlayers())
                    if (players.getGameMode() == GameMode.SURVIVAL && players.getWorld().getEnvironment() != Environment.NETHER)
                        players.damage(2);

        }

    }

}
