package me.noobsters.minigame.gui.types;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import me.noobsters.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class ConfirmationGui extends CustomGui {
    private @Getter Consumer<InventoryClickEvent> confirm;
    private @Getter Consumer<InventoryClickEvent> deny;

    public ConfirmationGui(RapidInv gui, Consumer<InventoryClickEvent> confirm, Consumer<InventoryClickEvent> deny) {
        super(gui);
        this.confirm = confirm;
        this.deny = deny;

        update();
    }

    @Override
    public void update(){
        var gui = getGui();
        gui.setItem(1, new ItemBuilder(Material.LIME_WOOL).name(ChatColor.GREEN + "Confirm").build(), confirm);
        gui.setItem(3, new ItemBuilder(Material.RED_WOOL).name(ChatColor.RED + "Cancel").build(), deny);
    }

}
