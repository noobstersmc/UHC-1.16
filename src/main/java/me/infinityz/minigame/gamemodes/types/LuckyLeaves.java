package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class LuckyLeaves extends IGamemode implements Listener {
    private UHC instance;

    public LuckyLeaves(UHC instance) {
        super("LuckyLeaves", "Trees have a chance to drop golden apples.", Material.GOLDEN_APPLE);
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
    public void onLeaf(LeavesDecayEvent e) {
        if (Math.random() <= ((instance.getGame().getApplerate()/2) / 100)) {
            e.getBlock().setType(Material.AIR);
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),
                    new ItemStack(Material.GOLDEN_APPLE));
        }

    }



}