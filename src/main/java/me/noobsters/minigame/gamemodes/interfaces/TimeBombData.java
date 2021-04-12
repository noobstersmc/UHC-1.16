package me.noobsters.minigame.gamemodes.interfaces;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

import lombok.Data;

@Data
public class TimeBombData {
    ArmorStand armorStand;
    Block left;
    Block right;

    public TimeBombData(ArmorStand armorStand, Block left, Block right){
        this.armorStand = armorStand;
        this.left = left;
        this.right = right;
    }
}
