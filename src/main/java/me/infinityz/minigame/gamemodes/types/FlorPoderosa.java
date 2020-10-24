package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class FlorPoderosa extends IGamemode implements Listener {
    private UHC instance;
    private ArrayList<Material> possibleDrops = new ArrayList<>();
    private Random random = new Random();

    public FlorPoderosa(UHC instance) {
        super("Flor Poderosa", "Las flores dropean cosas poderosas.");
        this.instance = instance;

        for (var materials : Material.values()) {
            var mString = materials.toString();
            if (!materials.isItem() || mString.contains("LEGACY") || mString.contains("COMMAND")
                    || mString.contains("AIR"))
                continue;
            possibleDrops.add(materials);
        }
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled()) {
            return false;
        }
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled()) {
            return false;
        }
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        // Test for and find if block broken was a flower
        //Call getRandomDropWithBooks to get the drop

    }

    private ItemStack getRandomDropWithBooks() {
        var randomDrop = getRandomDrop();
        if (randomDrop.getType() == Material.ENCHANTED_BOOK) {
            var meta = (EnchantmentStorageMeta) randomDrop.getItemMeta();
            var enchant = getRandomEnchant();
            meta.addStoredEnchant(enchant, random.nextInt(enchant.getMaxLevel()), true);
        }
        return randomDrop;
    }

    private Enchantment getRandomEnchant() {
        return Enchantment.values()[random.nextInt(Enchantment.values().length)];
    }

    private ItemStack getRandomDrop() {
        var randomMaterial = possibleDrops.get(random.nextInt(possibleDrops.size()));
        return new ItemStack(randomMaterial, random.nextInt(randomMaterial.getMaxStackSize()) + 1);
    }

}
