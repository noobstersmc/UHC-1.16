package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.GameTickEvent;
import me.noobsters.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class FallOut extends IGamemode implements Listener {
    private boolean damage = false;
    private float extradamage = 6;
    private float exda = 0;
    private int delay = 10;
    private UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    public FallOut(UHC instance) {
        super("FallOut", "Players that stay above Y=40 periodically\nrecieve damage after border time.", Material.BEDROCK);
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new FallOutCMD());
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
                        && players.getLocation().getY() > 40)
                            players.damage(2+exda);
                });
            });
        }
  
    }

    @CommandPermission("uhc.scenarios")
    @CommandAlias("FallOut")
    public class FallOutCMD extends BaseCommand {

        @Default
        public void toggleDamage(CommandSender sender) {
            damage = !damage;
            if(damage == true){
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                Bukkit.broadcast(senderName + ChatColor.YELLOW + "FallOut Damage switch to: " + damage, permissionDebug);
                Bukkit.broadcastMessage(ChatColor.of("#7aac2f") + "Go below coordinate Y=40 now. Player's that remain in surface will take a heart of damage every " + delay + " seconds.");
            }

        }

        @Subcommand("extradamage")
        @CommandAlias("extradamage")
        public void extraDamage(CommandSender sender, Float ed) {
            extradamage = ed;
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "FallOut extra damage set to " + ed/2 + " hearts.", permissionDebug);

        }

        @Subcommand("delay")
        @CommandAlias("delay")
        public void delay(CommandSender sender, Integer de) {
            delay = de;
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "FallOut delay set to " + de + " seconds.", permissionDebug);

        }
    }

}