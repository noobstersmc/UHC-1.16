package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class DamageCycle extends IGamemode implements Listener {
    private UHC instance;
    private Random random = new Random();
    private DamageCause currentDamage = DamageCause.LIGHTNING;
    private DamageCause[] DAMAGE_CAUSES = new DamageCause[] { 
        DamageCause.LAVA, 
        DamageCause.FIRE, // FIRETICK
        DamageCause.HOT_FLOOR, 
        DamageCause.FALL,
        DamageCause.ENTITY_EXPLOSION, // entity expl
        DamageCause.SUFFOCATION,
        DamageCause.PROJECTILE, 
        DamageCause.CONTACT, 
        DamageCause.MAGIC,
        DamageCause.ENTITY_ATTACK, // only mobs
        DamageCause.DROWNING,
        DamageCause.POISON,
        DamageCause.WITHER,
        DamageCause.STARVATION, 
        DamageCause.THORNS 
    };

    public DamageCycle(UHC instance) {
        super("Damage Cycle",
                "Every 5 minutes a damage type will be choosed randomly, if you take that type of damage you died.");
        this.instance = instance;
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

        if (e.getSecond() % 300 == 0) {
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
        var shouldDie = false;
        if ((cause == DamageCause.FIRE_TICK || cause == DamageCause.FIRE) && currentDamage == DamageCause.FIRE) {
            shouldDie = true;
        } else if ((cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) && currentDamage == DamageCause.ENTITY_EXPLOSION) {
            shouldDie = true;
        }else if(cause == currentDamage){
            shouldDie = true;
        }
        if(cause == DamageCause.ENTITY_ATTACK)
            shouldDie = false;
        
        if(shouldDie){
            e.setDamage(1000);
            Bukkit.broadcastMessage(ChatColor.of("#e00b87") + "DAMAGE CYCLE! BUAJAJA!");
            Bukkit.getOnlinePlayers().forEach(players -> {
            players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 0.1f);
            });
        }

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent e){
            var entity = e.getEntity();
            if(entity instanceof Player) return;
            if(entity instanceof Projectile) return;
        if(currentDamage == DamageCause.ENTITY_ATTACK){
            e.setDamage(1000);
            Bukkit.broadcastMessage(ChatColor.of("#e00b87") + "DAMAGE CYCLE! BUAJAJA!");
            Bukkit.getOnlinePlayers().forEach(players -> {
            players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 0.1f);
            });
        }
    }

}