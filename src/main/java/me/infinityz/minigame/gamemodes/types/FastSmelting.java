package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class FastSmelting extends IGamemode implements Listener {
    private UHC instance;

    public FastSmelting(UHC instance) {
        super("FastSmelting", "Item smelting is speed up 5 times.", Material.BLAST_FURNACE);
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
    public void onFurnaceBurn(FurnaceBurnEvent e){
        Block block = e.getBlock();
        
        int speed = 10;

        Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
            @Override
            public void run() {

                if (block.getType() == Material.AIR){
                    return;
                }
                Furnace furnace = (Furnace) block.getState();

                if (furnace.getBurnTime() <= 10) {
                    return;
                }

                if (furnace.getCookTime() <= 0){
                    Bukkit.getScheduler().runTaskLater(instance, this, 5);
                    return;
                }

                short newCookTime = (short) (furnace.getCookTime() + speed);

                if (newCookTime >= 200){
                    newCookTime = 199;
                }

                furnace.setCookTime(newCookTime);
                furnace.update();
                Bukkit.getScheduler().runTaskLater(instance, this, 2);
            }
        }, 1);
    }

}