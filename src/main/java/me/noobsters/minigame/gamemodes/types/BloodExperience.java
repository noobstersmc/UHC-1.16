package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

public class BloodExperience extends IGamemode implements Listener {
    private UHC instance;

    public BloodExperience(UHC instance) {
        super("BloodExperience", "For each level consumed the player\nwill take half a heart of damage.", Material.EXPERIENCE_BOTTLE);
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
        if(diff > 0 && hp-diff > 0){
            e.getPlayer().setHealth(hp-diff);
            e.getPlayer().damage(0.01);
        }
    }

      

}