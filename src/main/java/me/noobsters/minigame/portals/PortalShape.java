package me.noobsters.minigame.portals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

import lombok.Getter;

public class PortalShape {
    private @Getter PortalDirection direction;
    private @Getter Location target;
    private @Getter Block[][] blocks;
    private @Getter int rowSize;
    private @Getter int colummSize;

    /**
     * @param location Where to look at
     */
    public PortalShape(Location location, int row, int columm) {
        this.target = location;
        this.blocks = new Block[row][columm];
        this.rowSize = row;
        this.colummSize = columm;

        setPortalDirection(location.getBlock());
        completeBlockMatrix(location.getBlock());

    }

    public static PortalShape of(Location location, int row, int columm) {
        return new PortalShape(location, row, columm);
    }
    public static PortalShape of(Location location) {
        return new PortalShape(location, 3, 2);
    }

    public Location getTeleportLocation() {
        var b1 = blocks[1][0];
        var b2 = blocks[1][1];
        return new Location(b1.getWorld(), (b1.getX() + b2.getX()) / 2.0D, (b1.getY() + b2.getY()) / 2.0D,
                (b1.getZ() + b2.getZ()) / 2.0D);
    }

    public Location getTeleportLocation(Entity player) {
        var b1 = blocks[1][0];
        var b2 = blocks[1][1];
        return new Location(b1.getWorld(), (b1.getX() + b2.getX()) / 2.0D, (b1.getY() + b2.getY()) / 2.0D,
                (b1.getZ() + b2.getZ()) / 2.0D, player.getLocation().getYaw(), player.getLocation().getPitch());
    }

    private void completeBlockMatrix(Block block) {
        var right = getFromDirectrix(Directrix.RIGHT);
        var left = getFromDirectrix(Directrix.LEFT);
        var up = BlockFace.UP;
        var down = BlockFace.DOWN;

        var arrB = new Block[3];

        var upBlock = block.getRelative(up);
        var downBlock = block.getRelative(down);

        // Find portal blocks in 1D
        if (isPortalBlock(upBlock) && isPortalBlock(downBlock)) {
            arrB[2] = upBlock;
            arrB[1] = block;
            arrB[0] = downBlock;

        } else if (isPortalBlock(upBlock)) {
            var upperBlock = upBlock.getRelative(up);
            if (isPortalBlock(upperBlock)) {
                arrB[2] = upperBlock;
                arrB[1] = upBlock;
                arrB[0] = block;
            } else {
                arrB[2] = upBlock;
                arrB[1] = block;
                arrB[0] = downBlock;
            }

        } else if (isPortalBlock(downBlock)) {
            var downnerBlock = downBlock.getRelative(down);
            if (isPortalBlock(downnerBlock)) {
                arrB[2] = block;
                arrB[1] = downBlock;
                arrB[0] = downnerBlock;
            } else {
                arrB[2] = upBlock;
                arrB[1] = block;
                arrB[0] = downBlock;
            }

        }

        // Find blocks on either right or left side

        var rightBlock = arrB[0].getRelative(right);
        var leftBlock = arrB[0].getRelative(left);

        if (isPortalBlock(rightBlock)) {
            blocks[2][0] = arrB[2];
            blocks[1][0] = arrB[1];
            blocks[0][0] = arrB[0];

            blocks[2][1] = arrB[2].getRelative(right);
            blocks[1][1] = arrB[1].getRelative(right);
            blocks[0][1] = arrB[0].getRelative(right);

        } else if (isPortalBlock(leftBlock)) {
            blocks[2][1] = arrB[2];
            blocks[1][1] = arrB[1];
            blocks[0][1] = arrB[0];

            blocks[2][0] = arrB[2].getRelative(left);
            blocks[1][0] = arrB[1].getRelative(left);
            blocks[0][0] = arrB[0].getRelative(left);

        }

    }

    /**
     * Local directrix
     */
    private enum Directrix {
        UP, DOWN, LEFT, RIGHT, SELF;
    }

    private enum PortalDirection {
        SOUTH_NORTH, EAST_WEST;
    }

    private BlockFace getFromDirectrix(Directrix directrix) {
        BlockFace bf = null;
        switch (directrix) {
            case DOWN:
                return BlockFace.DOWN;
            case LEFT:
                return direction == PortalDirection.SOUTH_NORTH ? BlockFace.SOUTH : BlockFace.EAST;
            case RIGHT:
                return direction == PortalDirection.SOUTH_NORTH ? BlockFace.NORTH : BlockFace.WEST;
            case UP:
                return BlockFace.UP;
            default:
                return bf;
        }
    }

    private void setPortalDirection(Block block) {
        if (isPortalBlock(block.getRelative(BlockFace.NORTH)) || isPortalBlock(block.getRelative(BlockFace.SOUTH))) {
            direction = PortalDirection.SOUTH_NORTH;
        } else if (isPortalBlock(block.getRelative(BlockFace.EAST))
                || isPortalBlock(block.getRelative(BlockFace.WEST))) {
            direction = PortalDirection.EAST_WEST;
        }
    }

    private boolean isPortalBlock(Block b) {
        return b.getType() == Material.NETHER_PORTAL;
    }

}
