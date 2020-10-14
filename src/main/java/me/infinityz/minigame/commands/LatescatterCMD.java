package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.gamemodes.types.AdvancementHunter;
import me.infinityz.minigame.gamemodes.types.GoneFishing;
import me.infinityz.minigame.gamemodes.types.InfiniteEnchanter;
import me.infinityz.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("latescatter|ls|play")
public class LatescatterCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Default
    @Conditions("ingame|time:max=1200")
    public void lateScatter(@Conditions("hasNotDied|spec") UHCPlayer uhcPlayer) {
        var player = Bukkit.getPlayer(uhcPlayer.getUUID());

        uhcPlayer.setAlive(true);

        player.sendMessage((ChatColor.of("#7ab83c") + "You have been scattered into the world."));

        player.teleport(ChunksManager.findScatterLocation(Bukkit.getWorlds().get(0),
                (int) Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2));
        player.setGameMode(GameMode.SURVIVAL);
        
        if(instance.getGamemodeManager().isScenarioEnable(GoneFishing.class)){
            var item = new ItemStack(Material.FISHING_ROD);
            var meta = item.getItemMeta();
            meta.addEnchant(Enchantment.LURE, 69420, true);
            meta.addEnchant(Enchantment.LUCK, 69420, true);
            meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
        }
        
        if(instance.getGamemodeManager().isScenarioEnable(InfiniteEnchanter.class)){
            player.getInventory().addItem(new ItemStack(Material.BOOK, 32));
            player.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 32));
            player.getInventory().addItem(new ItemStack(Material.ANVIL, 8));
            player.getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE, 8));
            player.setLevel(100);
        }
        
        if(instance.getGamemodeManager().isScenarioEnable(AdvancementHunter.class)){
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
        }   
    }

}