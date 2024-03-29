package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.ExperienceOrb.SpawnReason;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

public class HasteyBabies extends IGamemode implements Listener {
    private UHC instance;

    public HasteyBabies(UHC instance) {
        super("HasteyBabies", "Tools come pre-enchanted\nwith efficiency 1 and unbreaking 1.", Material.IRON_PICKAXE);
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
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        if (isTool(result.getType())) {
            ItemStack stack = result.clone();
            ItemMeta meta = stack.getItemMeta();
            meta.addEnchant(Enchantment.DIG_SPEED, 1, false);
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            stack.setItemMeta(meta);
            e.getInventory().setResult(stack);
        }

    }

    @EventHandler
    public void onGrindstone(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.GRINDSTONE) {
            var grindInv = (GrindstoneInventory) e.getInventory();
            var slot0 = grindInv.getContents()[0];
            var slot1 = grindInv.getContents()[1];
            if (hasEnchanments(slot0) || hasEnchanments(slot1)) {
                Bukkit.getScheduler().runTask(instance, ()->{
                    grindInv.getLocation().getNearbyEntitiesByType(ExperienceOrb.class, 1).forEach(it->{
                        if(it.getSpawnReason() == SpawnReason.GRINDSTONE)
                            it.setExperience(0);
                        
                    });

                });
            }
        }
    }

    boolean hasEnchanments(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta())
            return false;

        var meta = stack.getItemMeta();
        return meta.getEnchantLevel(Enchantment.DIG_SPEED) == 1 && meta.getEnchantLevel(Enchantment.DURABILITY) == 1;
    }

    boolean isTool(Material material) {
        var materialName = material.toString();
        return material != null && materialName.contains("AXE") || materialName.contains("SHOVEL")
                || materialName.contains("HOE") || materialName.contains("SHEARS");
    }

}