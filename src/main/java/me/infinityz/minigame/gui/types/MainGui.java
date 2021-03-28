package me.infinityz.minigame.gui.types;

import org.bukkit.Material;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class MainGui extends CustomGui {

    UHC instance = UHC.getInstance();

    public MainGui(RapidInv gui) {
        super(gui);

        update();
    }

    @Override
    public void update(){
        var gui = getGui();
        //gui.setItem(1, new ItemBuilder(Material.LIME_WOOL).name(ChatColor.GREEN + "Confirm").build(), confirm);
        
    }

}
