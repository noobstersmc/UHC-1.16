package me.infinityz.minigame.gui.types;

import org.bukkit.Material;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class EnabledScenarios extends CustomGui {
    
    public EnabledScenarios(RapidInv gui) {
        super(gui);

        update();
        
    }

    @Override
    public void update(){

        var gamemodes = UHC.getInstance().getGamemodeManager().getEnabledGamemodes();
        var gui = getGui();
        gui.clearAllItems();

        if(gamemodes.isEmpty()){
            //vanilla+
            var vanilla = new ItemBuilder(Material.CAMPFIRE).name(ChatColor.YELLOW + "Vanilla+").addLore(ChatColor.WHITE + "Classic Minecraft Vanilla UHC experience.").build();
            gui.setItem(0, vanilla);
        }else{
            //gamemodes
            var contents = gui.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                IGamemode gamemode = (IGamemode) gamemodes.toArray()[i];
                var stack = new ItemBuilder(gamemode.getIcon()).name(ChatColor.YELLOW + gamemode.getName()).addLore(ChatColor.WHITE + gamemode.getDescription()).build();
                gui.setItem(i, stack);
            }

        }
    }
    
}