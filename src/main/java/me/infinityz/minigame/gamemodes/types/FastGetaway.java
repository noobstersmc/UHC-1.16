package me.infinityz.minigame.gamemodes.types;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class FastGetaway extends IGamemode implements Listener {
    private UHC instance;

    public FastGetaway(UHC instance) {
        super("FastGetaway",
                "For each kill players get 1 minute of speed 2.");
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
    public void onDamage(EntityDeathEvent e){
        if(e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player){
            var player = (Player) e.getEntity().getKiller();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 120, 1));
        }
    }

}