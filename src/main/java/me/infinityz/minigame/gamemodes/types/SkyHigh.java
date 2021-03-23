package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

public class SkyHigh extends IGamemode implements Listener {
    private boolean damage = false;
    private float extradamage = 6;
    private float exda = 0;
    private int delay = 10;
    private UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    public SkyHigh(UHC instance) {
        super("SkyHigh", "Players that stay below Y=150 periodically recieve damage after border time.");
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new SkyHighCMD());
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);

        setEnabled(false);
        return true;
    }


    @EventHandler//condition damage
    public void onStart(GameTickEvent e) {
        if(e.getSecond() == instance.getGame().getBorderTime())
            damage = true;
        if(e.getSecond() == instance.getGame().getBorderTime()+instance.getGame().getBorderCenterTime())
            exda = extradamage;
        if (damage && e.getSecond() % delay == 0) {
            Bukkit.getScheduler().runTask(instance, ()->{
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (players.getGameMode() == GameMode.SURVIVAL 
                        && players.getWorld().getEnvironment() != Environment.NETHER
                        && players.getLocation().getY() < 150)
                            players.damage(2+exda);
                });
            });
        }
  
    }

    @CommandPermission("uhc.scenarios")
    @CommandAlias("skyhigh")
    public class SkyHighCMD extends BaseCommand {

        @Default
        public void toggleDamage(CommandSender sender) {
            damage = !damage;
            if(damage == true){
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                Bukkit.broadcast(senderName + ChatColor.YELLOW + "SkyHigh Damage switch to: " + damage, permissionDebug);
                Bukkit.broadcastMessage(ChatColor.of("#7fe5f0") + "Go above coordinate Y=150 now. Player's that remain in surface will take a heart of damage every " + delay + " seconds.");
            }

        }

        @Subcommand("extradamage")
        @CommandAlias("extradamage")
        public void extraDamage(CommandSender sender, Float ed) {
            extradamage = ed;
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "SkyHigh extra damage set to " + ed/2 + " hearts.", permissionDebug);

        }

        @Subcommand("delay")
        @CommandAlias("delay")
        public void delay(CommandSender sender, Integer de) {
            delay = de;
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "SkyHigh delay set to " + de + " seconds.", permissionDebug);

        }
    }

}