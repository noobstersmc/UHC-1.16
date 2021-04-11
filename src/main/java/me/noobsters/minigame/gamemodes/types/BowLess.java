package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class BowLess extends IGamemode implements Listener {
    private UHC instance;

    public BowLess(UHC instance) {
        super("BowLess", "Bows are disabled.", Material.BOW);
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
    public void onCraft(PrepareItemCraftEvent e) {
        var recipe = e.getRecipe();
        if(recipe != null && recipe.getResult().getType() == Material.BOW){
            e.getInventory().setResult(null);
        }

    }

    @EventHandler
    public void onDrop(ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getType().equals(Material.BOW))
            e.setCancelled(true);

    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onHold(PlayerInteractEvent e) {
        final var player = e.getPlayer();
        final var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.BOW) {
            player.getInventory().setItemInMainHand(null);
            player.sendMessage(ChatColor.RED + "Bows are disabled.");
        }
    }
    
}