package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

public class HeavyPockets extends IGamemode implements Listener {
    private UHC instance;

    public HeavyPockets(UHC instance) {
        super("Heavy Pockets", "Players drop netherite 2 scrap.", Material.NETHERITE_SCRAP);
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
    public void onDeath(PlayerDeathEvent e){
        e.getDrops().add(new ItemStack(Material.NETHERITE_SCRAP, 2));

    }

}