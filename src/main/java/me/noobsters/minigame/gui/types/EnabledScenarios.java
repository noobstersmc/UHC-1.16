package me.noobsters.minigame.gui.types;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import fr.mrmicky.fastinv.ItemBuilder;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class EnabledScenarios extends CustomGui {

    UHC instance = UHC.getInstance();
    
    public EnabledScenarios(RapidInv gui) {
        super(gui);

        update();

    }

    @Override
    public void update() {

        var gamemodes = instance.getGamemodeManager().getEnabledGamemodes();
        var gui = getGui();
        gui.clearAllItems();

        if (gamemodes.isEmpty()) {
            // vanilla+
            var vanilla = new ItemBuilder(Material.CAMPFIRE).name(ChatColor.YELLOW + "Vanilla+")
                    .addLore(ChatColor.WHITE + "Classic Minecraft Vanilla UHC experience.").build();
            gui.setItem(0, vanilla);
        } else {
            // add them
            int i = 0;
            int size = gui.getInventory().getContents().length;
            for (var gm : gamemodes) {
                if(i >= size-1) break;
                gui.setItem(i, gm.getAsIcon().build());
                i++;
            }

        }

        gui.setItem(17, new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.GREEN + "UHC Game").build(), action -> {
            var player = (Player) action.getWhoClicked();
            instance.getGuiManager().getMainGui().open((Player) action.getWhoClicked());
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
        });
    }

}