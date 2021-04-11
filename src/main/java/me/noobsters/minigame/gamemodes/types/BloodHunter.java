package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

public class BloodHunter extends IGamemode implements Listener {
    private UHC instance;

    public BloodHunter(UHC instance) {
        super("BloodHunter", "Players get 1 extra red heart for each kill.", Material.DIAMOND_SWORD);
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
    public void onKill(PlayerDeathEvent e){
        var killer = e.getEntity().getPlayer().getKiller();
        if(killer == null) return;
        var killerMaxHealth = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(killerMaxHealth+2);
    }

}