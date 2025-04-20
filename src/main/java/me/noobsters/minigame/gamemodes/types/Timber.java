package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

import java.util.HashSet;
import java.util.Set;

public class Timber extends IGamemode implements Listener {
    private UHC instance;
    private final Set<Block> brokenBlocks = new HashSet<>(); // Keep track of broken blocks to prevent infinite recursion

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
            // Clear the set before each tree breaking attempt
            brokenBlocks.clear();
            breakTree(block, e.getPlayer());
        }
    }

    private void breakTree(Block block, Player player) {
        if (block == null || brokenBlocks.contains(block) || !block.getWorld().isChunkLoaded(block.getChunk())) {
            return; // Stop if block is null, already broken, or chunk is unloaded
        }

        Material type = block.getType();
        if (isLog(type)) {
            brokenBlocks.add(block); // Mark the block as broken
            ItemStack item = new ItemStack(Material.AIR);

            // Use a try-catch to handle any unexpected errors during breaking
            try {
                block.breakNaturally(item, true);
            } catch (Exception e) {
                // Log the error or handle it appropriately.  Crucial for debugging!
                e.printStackTrace();  // Print stack trace for debugging
                return; // Stop the recursion if breaking fails

            }


            //Iterate around the block. Instead of face values, directly get the surrounding blocks
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        // Skip the center block (the block we're currently breaking)
                        if (x == 0 && y == 0 && z == 0) continue;


                        Block relativeBlock = block.getRelative(x, y, z);

                        Bukkit.getScheduler().runTaskLater(instance, () -> {
                            breakTree(relativeBlock, player);
                        }, 1); //Short delay to avoid overloading the server.

                    }
                }
            }
        }

    }

}