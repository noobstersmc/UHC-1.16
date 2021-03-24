package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class VengefulSpirits extends IGamemode implements Listener {
    private UHC instance;

    public VengefulSpirits(UHC instance) {
        super("VengefulSpirits", "When a player dies below coordinate Y=50 spawns a blaze.\nAbove that coordinate will spawn a Ghast.", Material.GHAST_TEAR);
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
    public void onDeath(EntityDeathEvent e){
        if(e.getEntity() instanceof Player){
            var player = (Player) e.getEntity();
            if(player.getLocation().getY() < 50){
                player.getWorld().spawnEntity(player.getLocation(), EntityType.BLAZE);
            }else{
                player.getWorld().spawnEntity(player.getLocation().add(0, 20, 0), EntityType.GHAST);
            }
        }
    }
    

      

}