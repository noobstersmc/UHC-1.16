package me.infinityz.minigame.commands;

import org.bukkit.entity.Player;

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
        var mainHandItem = player.getInventory().getItemInMainHand();

    }
}

