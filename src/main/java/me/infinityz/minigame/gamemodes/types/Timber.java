package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class Timber extends IGamemode implements Listener {
    private UHC instance;

    public Timber(UHC instance) {
        super("Timber", "Logs fall in chain.");
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

    private boolean isLog(Material material){
        return (
                material.equals(Material.ACACIA_LOG) ||
                        material.equals(Material.BIRCH_LOG) ||
                        material.equals(Material.DARK_OAK_LOG) ||
                        material.equals(Material.JUNGLE_LOG) ||
                        material.equals(Material.OAK_LOG) ||
                        material.equals(Material.SPRUCE_LOG)
        );
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (isLog(block.getType())) {
            breakTree(block);
        }
    }


    private void breakTree(Block block) {
        var item = new ItemStack(Material.AIR);
        var i = 0;
        if (isLog(block.getType())){
            block.breakNaturally(item, true);
            i = 2;
        }else {
            i--;
        }
        if (i > 0){
            for (BlockFace face : BlockFace.values()) {
                if (face.equals(BlockFace.DOWN) || face.equals(BlockFace.UP) || face.equals(BlockFace.NORTH) ||
                        face.equals(BlockFace.EAST) || face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST)) {
                    UHC.newChain().delay(3).sync(() -> breakTree(block.getRelative(face))).sync(TaskChain::abort).execute();
                }
            }
        }
    }



}