package me.infinityz.minigame.gui.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.gui.CustomGui;
import net.noobsters.kern.paper.guis.RapidInv;

public class CustomCraftTable extends CustomGui {

    List<ItemStack> items = new ArrayList<>();
    EnabledCrafting enabledCrafting;

    public CustomCraftTable(RapidInv gui, List<ItemStack> items, EnabledCrafting enabledCrafting) {
        super(gui);
        this.items = items;
        this.enabledCrafting = enabledCrafting;
        update();

    }

    @Override
    public void update() {
        var gui = getGui();
        var i = 0;
        for (ItemStack itemStack : items) {
            if(itemStack != null){
                gui.setItem(i, itemStack, action->{
                    enabledCrafting.getGui().open((Player) action.getWhoClicked());
                });
            }
            i++;
        }
    }
}
