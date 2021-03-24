package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class GoToHell extends IGamemode implements Listener {
    private boolean damage = false;
    private float damageval = 3;
    private int delay = 10;
    private UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    public GoToHell(UHC instance) {
        super("Go to Hell", "Players that stay in the overworld will periodically recieve damage after border time.", Material.CRIMSON_NYLIUM);
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new GoToHellCMD());
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        this.instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        this.instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    /**
     * InnerGoToHell
     */

    @EventHandler // condition damage
    public void onStart(GameTickEvent e) {
        if(e.getSecond() == instance.getGame().getBorderTime())
            damage = true;
        if (damage && e.getSecond() % delay == 0) {
        Bukkit.getScheduler().runTask(instance, ()->{
        
            Bukkit.getOnlinePlayers().forEach(players -> {
                if (players.getGameMode() == GameMode.SURVIVAL
                        && players.getWorld().getEnvironment() != Environment.NETHER)
                    players.damage(damageval);
            });
        
        });
        }

    }

    @CommandPermission("uhc.scenarios")
    @CommandAlias("gotohell")
    public class GoToHellCMD extends BaseCommand {

        @Default
        @Subcommand("damage")
        public void toggleDamage(CommandSender sender) {
            damage = !damage;
            if(damage == true){
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                Bukkit.broadcast(senderName + ChatColor.YELLOW + "Go To Hell Damage switch to: " + damage + " hearts.", permissionDebug);
                Bukkit.broadcastMessage(ChatColor.of("#cd4619")
                    + "Go to the Nether now. Player's that remain in the overworld will take "+ damageval + " hearts of damage every " + delay + " seconds.");
            }
        }

        @Subcommand("delay")
        @CommandAlias("delay")
        public void delay(CommandSender sender, Integer newDelay) {
            delay = newDelay;
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "GoToHell delay set to " + newDelay + " seconds.", permissionDebug);

        }

        @Subcommand("damagevalue")
        @CommandAlias("damagevalue")
        public void extradamage(CommandSender sender, Float damagevalue) {
            damageval = damagevalue;
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "Go To Hell damage value set to " + damagevalue/2 + " hearts.", permissionDebug);

        }

    }

}
