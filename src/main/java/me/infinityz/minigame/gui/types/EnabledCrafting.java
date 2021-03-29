package me.infinityz.minigame.gui.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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

    UHC instance = UHC.getInstance();
    HashMap<String, CustomCraftTable> tables = new HashMap<>();
    String permissionConfig = "uhc.config.cmd";

    public EnabledCrafting(RapidInv gui) {
        super(gui);

        var crafts = UHC.getInstance().getCraftingManager().getAllRecipes().entrySet().stream().collect(Collectors.toList());

        for (var craft : crafts) {
            var name = craft.getKey();
            var recipe = craft.getValue().getRecipe();
            List<ItemStack> list = new ArrayList<>();

            if (recipe instanceof ShapedRecipe) {
                var shaped = (ShapedRecipe) recipe;
                list = getShapedRecipe(shaped);
            } else if (recipe instanceof ShapelessRecipe) {
                var shapeLess = (ShapelessRecipe) recipe;
                list = getShapeLessRecipe(shapeLess);
            }

            tables.put(name, new CustomCraftTable(new RapidInv(InventoryType.DISPENSER, name), list));
        }
        update();

    }

    @Override
    public void update() {

        var crafts = instance.getCraftingManager().getAllRecipes().entrySet().stream()
                .filter(recipe -> recipe.getValue().isEnabled()).collect(Collectors.toList());

        var gui = getGui();
        gui.clearAllItems();

        if (crafts.isEmpty()) {
            var result = new ItemBuilder(Material.CRAFTING_TABLE).name(ChatColor.YELLOW + "Vanilla Crafts")
                    .flags(ItemFlag.HIDE_ATTRIBUTES).build();

            gui.setItem(0, result, action->{
                var player = (Player) action.getWhoClicked();
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
                
            });

        } else if (!crafts.isEmpty()) {
            // add them
            int i = 0;
            int size = gui.getInventory().getContents().length;
            for (var craft : crafts) {
                if (i >= size)
                    break;
                var name = craft.getKey();
                var result = new ItemBuilder(craft.getValue().getRecipe().getResult()).name(ChatColor.YELLOW + name)
                        .flags(ItemFlag.HIDE_ATTRIBUTES).build();

                gui.setItem(i, result, action->{
                    var player = (Player) action.getWhoClicked();
                    if(action.getClick() == ClickType.RIGHT){
                        tables.get(name).open(player);
                    }else{
                        instance.getGuiManager().getMainGui().open(player);
                        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
                    }
                    
                });
                

                i++;
            }

        }
    }

    public List<ItemStack> getShapedRecipe(ShapedRecipe recipe) {
        var choice = recipe.getIngredientMap();
        var shape = recipe.getShape();

        List<ItemStack> list = new ArrayList<>();
        for (var line : shape) {
            for (var slot : line.toCharArray()) {
                list.add(choice.get(slot));
            }
        }
        
        return list;
    }

    public List<ItemStack> getShapeLessRecipe(ShapelessRecipe recipe) {
        return recipe.getIngredientList();
    }

}