package me.infinityz.minigame.gamemodes.types;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class SwordLess extends IGamemode implements Listener {
    private UHC instance;

    public SwordLess(UHC instance) {
        super("SwordLess", "Swords are disabled.");
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
        if(recipe != null && isSword(recipe.getResult())){
            e.getInventory().setResult(null);
        }

    }

    @EventHandler
    public void onDrop(ItemSpawnEvent e) {
        if (isSword(e.getEntity().getItemStack()))
            e.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHold(PlayerInteractEvent e) {
        final var player = e.getPlayer();
        final var item = player.getInventory().getItemInMainHand();
        if (isSword(item)) {
            player.getInventory().setItemInMainHand(null);
            player.sendMessage(ChatColor.RED + "Swords are disabled.");
        }
    }

    private boolean isSword(ItemStack e){
        return e != null && e.getType().toString().contains("_SWORD");
    }

}