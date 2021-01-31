package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class BareBones extends IGamemode implements Listener {
    private UHC instance;

    public BareBones(UHC instance) {
        super("BareBones", "Enchants, Diamonds and Gold is disabled. Players interesting items.");
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
    public void onDeath(PlayerDeathEvent e){
        e.getDrops().add(new ItemStack(Material.DIAMOND, 1));
        e.getDrops().add(new ItemStack(Material.STRING, 2));
        e.getDrops().add(new ItemStack(Material.ARROW, 16));

        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta im = goldenHead.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Golden Head");
        goldenHead.setItemMeta(im);
        e.getDrops().add(goldenHead);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        var recipe = e.getRecipe();
        if(recipe != null && (recipe.getResult().getType() == Material.ENCHANTING_TABLE || recipe.getResult().getType() == Material.ANVIL 
            || recipe.getResult().getType() == Material.ENCHANTING_TABLE)){
            e.getInventory().setResult(null);
        }

    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        var block = e.getBlock();
        if(block.getType() == Material.DIAMOND_ORE)
            block.setType(Material.IRON_INGOT);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.GOLD_INGOT) {
            stack.setType(Material.IRON_INGOT);
        }else if (type == Material.GOLD_ORE) {
            stack.setType(Material.IRON_ORE);
        }
    }

    @EventHandler
    public void onOpenChest(InventoryOpenEvent e){
        if(e.getInventory().getType().toString().equals("CHEST")){
            var items = e.getInventory().getContents();
            for (int i = 0; i < items.length; i++) {
                if(items[i] != null && (items[i].getType() == Material.DIAMOND || items[i].getType() == Material.GOLD_INGOT)){
                    items[i].setType(Material.COAL);
                }
            }
        }
    }

    @EventHandler
    public void onTrade(VillagerAcquireTradeEvent e){
        if(e.getRecipe().getResult().getType().toString().contains("DIAMOND")){
            e.setCancelled(true);
        }
    }

}