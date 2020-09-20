package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class HasteyBoys extends IGamemode implements Listener {
    private UHC instance;

    public HasteyBoys(UHC instance) {
        super("HasteyBoys", "Tools come pre-enchanted with efficiency and unbreaking.");
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
        var result = e.getInventory().getResult();
        if (isTool(result.getType())) {
            ItemStack stack = result.clone();
            ItemMeta meta = stack.getItemMeta();
            meta.addEnchant(Enchantment.DIG_SPEED, 3, false);
            meta.addEnchant(Enchantment.DURABILITY, 3, false);
            stack.setItemMeta(meta);
            e.getInventory().setResult(stack);
        }

    }

    boolean isTool(Material material) {
        var materialName = material.toString();
        return material != null && materialName.contains("AXE") || materialName.contains("SHOVEL")
                || materialName.contains("HOE");
    }

}