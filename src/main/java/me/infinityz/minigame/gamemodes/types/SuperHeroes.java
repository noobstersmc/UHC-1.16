package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class SuperHeroes extends IGamemode implements Listener {
    private UHC instance;
    private Random random = new Random();

    public SuperHeroes(UHC instance) {
        super("SuperHeroes",
                "Players get random super power, each power controls a potion effect.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        instance.getGame().setPotions(false);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        instance.getGame().setPotions(true);
        setEnabled(false);
        return true;
    }

    public void givePower(Player player){
        switch(random.nextInt(9)) {
            case 1: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 100000, 0));
            }break;
            case 2: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 100000, 1));
            }break;
            case 3: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 100000, 1));
            }break;
            case 4: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * 100000, 4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 4));
            }break;
            case 5: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 100000, 0));
            }break;
            case 6: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 100000, 0));
            }break;
            case 7: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 100000, 0));
            }break;
            case 8: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 100000, 0));
            }break;
            default: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 100000, 3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 100000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 100000, 0));

            }break;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinLate(PlayerJoinedLateEvent e){
        givePower(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStart(GameStartedEvent e) {
        Bukkit.getScheduler().runTask(instance, () -> {
            Bukkit.getOnlinePlayers().forEach(players->{
                givePower(players);
            });
        });

    }


}