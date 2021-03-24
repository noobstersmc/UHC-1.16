package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class Timber extends IGamemode implements Listener {
    private UHC instance;

    public Timber(UHC instance) {
        super("Timber", "Logs fall in chain.", Material.OAK_LOG);
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

    private boolean isLog(Material material) {
        return material.toString().toLowerCase().endsWith("_log");
    }


    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        
        if (isLog(block.getType())) {
            breakTree(block, e.getPlayer());
        }
    }

    private void breakTree(Block block, Player player) {
        var item = new ItemStack(Material.AIR);
        var type = block.getType();
        if (isLog(type)) {
            block.breakNaturally(item, true);
            for (BlockFace face : BlockFace.values()) {
                if (face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP) || face.equals(BlockFace.NORTH)
                        || face.equals(BlockFace.EAST) || face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST)) {
                            Bukkit.getScheduler().runTaskLater(instance, ()->{
                                breakTree(block.getRelative(face), player);
                            }, 3);
                }
            }

        }

    }

}