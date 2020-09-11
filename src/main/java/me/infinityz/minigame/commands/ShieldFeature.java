package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;

/**
 *Shield Feature
 */

@CommandAlias("shield")
public class ShieldFeature extends BaseCommand {
        
    @Default
    public void shieldCMD(Player sender){
        openLoom(sender);
         
    }

    public void openLoom(Player player){
        var inv  = Bukkit.createInventory(null, InventoryType.LOOM);
        player.openInventory(inv);

    }
}

