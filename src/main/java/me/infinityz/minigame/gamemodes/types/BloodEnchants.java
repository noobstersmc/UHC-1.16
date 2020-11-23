package me.infinityz.minigame.gamemodes.types;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class BloodEnchants extends IGamemode implements Listener {
    private UHC instance;

    public BloodEnchants(UHC instance) {
        super("BloodEnchants", "Enchants with a pinch of damage.");
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
    public void xpChange(PlayerLevelChangeEvent e){
        var diff = e.getOldLevel()-e.getNewLevel();
        var hp = e.getPlayer().getHealth();
        if(diff > 0){
            e.getPlayer().setHealth(hp-diff);
            e.getPlayer().damage(0.01);
        }
    }

      

}