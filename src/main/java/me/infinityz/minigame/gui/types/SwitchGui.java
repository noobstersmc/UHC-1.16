package me.infinityz.minigame.gui.types;

import java.text.DecimalFormat;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class SwitchGui extends CustomGui {

    @Getter @Setter double value = 0;
    @Getter @Setter double incrementer = 1;
    DecimalFormat numberFormat = new DecimalFormat("#0.0");

    public SwitchGui(RapidInv gui, float incrementer) {
        super(gui);

        this.incrementer = incrementer;
        update();
    }

    @Override
    public void update() {
        updateMinus();
        updatePlus();

    }

    public void updateMinus() {
        var gui = getGui();
        var formatted = numberFormat.format(value);
        var minus = new ItemBuilder(Material.TIPPED_ARROW).flags(ItemFlag.HIDE_POTION_EFFECTS)
                .name(ChatColor.YELLOW + "(-)").lore(ChatColor.AQUA + "Value: " + ChatColor.WHITE + formatted).build();
        var meta = (PotionMeta) minus.getItemMeta();
        meta.setColor(Color.fromRGB(255, 239, 0));
        minus.setItemMeta(meta);

        gui.setItem(0, minus, action -> {
            var player = (Player) action.getWhoClicked();
            if (value <= 0.1) {
                value = 0.0;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
            }else {
                value -= 1 * incrementer;

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 2);
            }
            updateMinus();
            updatePlus();
        });
    }

    public void updatePlus() {
        var gui = getGui();
        var formatted = numberFormat.format(value);
        var plus = new ItemBuilder(Material.TIPPED_ARROW).flags(ItemFlag.HIDE_POTION_EFFECTS)
                .name(ChatColor.GREEN + "(+)").lore(ChatColor.AQUA + "Value: " + ChatColor.WHITE + formatted).build();
        var meta = (PotionMeta) plus.getItemMeta();
        meta.setColor(Color.fromRGB(0, 255, 51));
        plus.setItemMeta(meta);

        gui.setItem(4, plus, action -> {
            var player = (Player) action.getWhoClicked();
            value += 1 * incrementer;

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 0.1f);
            updateMinus();
            updatePlus();
        });
    }

}
