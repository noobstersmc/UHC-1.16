package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

public class BloodDiamonds extends IGamemode implements Listener {
    private UHC instance;

    public BloodDiamonds(UHC instance) {
        super("BloodDiamonds", "When players mine a diamond they lose half a heart.", Material.DIAMOND_ORE);
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
    public void onBreak(BlockBreakEvent e){
        if(e.getBlock().getType() == Material.DIAMOND_ORE){
        var hp = e.getPlayer().getHealth();
            e.getPlayer().setHealth(hp-1);
            e.getPlayer().damage(0.01);
        
        }
    }



}