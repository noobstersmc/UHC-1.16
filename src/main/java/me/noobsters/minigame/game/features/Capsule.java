package me.noobsters.minigame.game.features;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class Capsule {

    private @Getter @Setter Location location;
    private @Getter @Setter boolean created = false;
    private @Getter @Setter boolean inUse = false;

    private Block[][][] capsule;


    public Capsule(Location location) {
        this.location = location;
        capsule = new Block[3][5][3];
    }

    public boolean notUsed(){
        return !inUse;
    }

    public boolean notUsedAndNotCreated(){
        if(created == false && inUse == false) return true;
        return false;
    }
    /**
     * Creates the capsule with the specified blocks.
     * @param mainBlock Block in corners and center of the platform and the roof.
     * @param decorationBlock Block in all sides.
     * @param panelBlock Block of glass.
     * @param roofBlock Slabs block in roof of the capsule.
     */
    public void create(Material mainBlock, Material decorationBlock, Material panelBlock, Material roofBlock) {
        final var b = location.clone().add(-1, -1.5f, -1).getBlock();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 3; z++) {
                    var relative = b.getRelative(x, y, z);
                    capsule[x][y][z] = relative;
                    if (y == 0) {
                        if (!(x == 1 && z == 1) && (x == 1 || z == 1)) {
                            relative.setType(decorationBlock);
                        } else {
                            relative.setType(mainBlock);
                        }
                    } else if (y == 4) {
                        if (x == 1 && z == 1) {
                            relative.setType(mainBlock);
                        } else {
                            relative.setType(roofBlock);
                        }
                    } else {
                        if (x == 1 && z == 1) {
                            relative.setType(Material.AIR);
                        } else {
                            relative.setType(panelBlock);
                        }
                    }
                }
            }
        }
        created = true;

    }

    /**
     * Destroy the capsule.
     */
    public void destroy(){
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 3; z++) {
                    var block = capsule[x][y][z];
                    block.setType(Material.GLASS);
                    block.breakNaturally(new ItemStack(Material.AIR), true);
                    
                }
            }
        }
        created = false;
    }

}
