package me.noobsters.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.GameTickEvent;
import me.noobsters.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class DamageCycle extends IGamemode implements Listener {
    private UHC instance;
    private String permissionDebug = "uhc.configchanges.see";
    private Random random = new Random();
    private int delay = 300;
    private DamageCause currentDamage = DamageCause.LIGHTNING;
    private DamageCause[] DAMAGE_CAUSES = new DamageCause[] { 
        DamageCause.LAVA, 
        DamageCause.FIRE, 
        DamageCause.HOT_FLOOR,
        DamageCause.FALL, 
        DamageCause.ENTITY_EXPLOSION, 
        DamageCause.SUFFOCATION, 
        DamageCause.PROJECTILE,
        DamageCause.CONTACT, 
        DamageCause.MAGIC,
        DamageCause.ENTITY_ATTACK, 
        DamageCause.DROWNING, 
        DamageCause.POISON,
        DamageCause.WITHER, 
        DamageCause.STARVATION, 
        DamageCause.THORNS };

    public DamageCycle(UHC instance) {
        super("Damage Cycle",
                "Every 5 minutes a damage type will be choosed randomly,\nIf a player take that type of damage died.", Material.CLOCK);
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new DamageCycleCMD());
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

    @EventHandler
    public void onGameLoop(GameTickEvent e) {

        if (e.getSecond() % delay == 0) {
            Bukkit.getScheduler().runTask(instance, () -> {
                currentDamage = DAMAGE_CAUSES[random.nextInt(DAMAGE_CAUSES.length)];
                Bukkit.broadcastMessage(
                        ChatColor.of("#a41ae0") + "DAMAGE CYCLE! Be carefull with " + currentDamage.toString());
                Bukkit.getOnlinePlayers().forEach(players -> {
                    players.playSound(players.getLocation(), Sound.ENTITY_BEE_HURT, 1, 0.1f);
                });
            });
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        var cause = e.getCause();

        if (currentDamage == DamageCause.ENTITY_ATTACK)
            return;

        if ((currentDamage == DamageCause.FIRE && (cause == DamageCause.FIRE_TICK || cause == DamageCause.FIRE))
                || (currentDamage == DamageCause.ENTITY_EXPLOSION
                        && (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION))
                || (cause == currentDamage)) {

            e.setDamage(1000);
            if (!(e.getEntity() instanceof Player))
                return;
            Bukkit.broadcastMessage(ChatColor.of("#e00b87") + "DAMAGE CYCLE! BUAHAHA!");
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 0.1f);
            });

        }

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent e) {
        var attacker = e.getDamager();
        if ((attacker instanceof Player) || (attacker instanceof Projectile))
            return;
        if (currentDamage == DamageCause.ENTITY_ATTACK) {
            e.setDamage(1000);
            if (!(e.getEntity() instanceof Player))
                return;
            Bukkit.broadcastMessage(ChatColor.of("#e00b87") + "DAMAGE CYCLE! BUAHAHA!");
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 0.1f);
            });
        }
    }

    @CommandPermission("uhc.scenarios")
    @CommandAlias("damagecycle")
    public class DamageCycleCMD extends BaseCommand {

        @Default
        public void changeDelay(CommandSender sender, Integer newDelay) {
            delay = newDelay*60;
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "Damage Cycle delay has change to: " + newDelay + " minutes.", permissionDebug);
        }

    }

}