package me.infinityz.minigame.gui;

import org.bukkit.entity.Player;

import net.noobsters.kern.paper.guis.RapidInv;

public abstract class CustomGui {
    RapidInv gui;

    public CustomGui(RapidInv gui){
        this.gui = gui;
    }

    public RapidInv getRapidInv() {
        return gui;
    }
    public void setRapidInv(RapidInv gui) {
        this.gui = gui;
    }

    public void open(Player player){
        gui.open(player);
    }

    public abstract void update();
    
}