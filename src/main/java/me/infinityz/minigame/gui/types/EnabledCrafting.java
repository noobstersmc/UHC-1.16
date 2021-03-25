package me.infinityz.minigame.gui.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class EnabledCrafting extends CustomGui {

    HashMap<NamespacedKey, CustomCraftTable> tables = new HashMap<>();

    public EnabledCrafting(RapidInv gui) {
        super(gui);

        var allRecipes = UHC.getInstance().getCraftingManager().getAllRecipes();

        for (var craft : allRecipes.values()) {
            var name = craft.getName();
            var recipe = craft.getRecipe();
            List<ItemStack> list = new ArrayList<>();

            if (recipe instanceof ShapedRecipe) {
                var shaped = (ShapedRecipe) recipe;
                list = getShapedRecipe(shaped);
            } else if (recipe instanceof ShapelessRecipe) {
                var shapeLess = (ShapelessRecipe) recipe;
                list = getShapeLessRecipe(shapeLess);
            }
            var key = craft.getNamespacedKey();

            tables.put(key,
                    new CustomCraftTable(new RapidInv(InventoryType.DISPENSER, name + " crafting"), list, this));
        }
        update();

    }

    @Override
    public void update() {

        var crafts = UHC.getInstance().getCraftingManager().getEnabledRecipes();
        var gui = getGui();
        gui.clearAllItems();
        tables.clear();

        if (!crafts.isEmpty()) {
            // add them
            int i = 0;
            int size = gui.getInventory().getContents().length;
            for (var craft : crafts) {
                if (i >= size)
                    break;
                var name = craft.getName();
                var key = craft.getNamespacedKey();
                var result = new ItemBuilder(craft.getRecipe().getResult()).name(ChatColor.YELLOW + name)
                        .flags(ItemFlag.HIDE_ATTRIBUTES).build();

                gui.setItem(i, result, action -> {
                    tables.get(key).open((Player) action.getWhoClicked());
                });

                i++;
            }

        }
    }

    public List<ItemStack> getShapedRecipe(ShapedRecipe recipe) {
        // fix
        return recipe.getIngredientMap().values().stream().collect(Collectors.toList());
    }

    public List<ItemStack> getShapeLessRecipe(ShapelessRecipe recipe) {
        return recipe.getIngredientList();
    }

}