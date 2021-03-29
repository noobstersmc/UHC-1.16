package me.infinityz.minigame.gui.types;

import java.text.DecimalFormat;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
    @Getter @Setter int downLimit = 0;
    @Getter @Setter int upLimit = 100;
    DecimalFormat numberFormat = new DecimalFormat("#0.0");
    String permissionConfig = "uhc.config.cmd";

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
                .name(ChatColor.YELLOW + "(-)").lore(ChatColor.AQUA + "Value: " + ChatColor.WHITE + formatted)
                .meta(PotionMeta.class, meta -> meta.setColor(Color.fromRGB(0, 0, 0))).build();

        gui.setItem(0, minus, action -> {
            var player = (Player) action.getWhoClicked();
            if (value - incrementer <= downLimit || value - incrementer*2 <= downLimit) {
                value = downLimit;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
            }else {
                if(action.getClick() == ClickType.RIGHT){
                    value -= incrementer*2;

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 2);
                }else{
                    value -= incrementer;

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 2);
                }

            }
            updateMinus();
            updatePlus();
        });
    }

    public void updatePlus() {
        var gui = getGui();
        var formatted = numberFormat.format(value);
        var plus = new ItemBuilder(Material.TIPPED_ARROW).flags(ItemFlag.HIDE_POTION_EFFECTS)
                .name(ChatColor.GREEN + "(+)").lore(ChatColor.AQUA + "Value: " + ChatColor.WHITE + formatted)
                .meta(PotionMeta.class, meta -> meta.setColor(Color.fromRGB(0, 255, 51))).build();

        gui.setItem(4, plus, action -> {
            var player = (Player) action.getWhoClicked();

            if (value + incrementer >= upLimit || value + incrementer*2 >= upLimit) {
                value = upLimit;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
            }else {
                if(action.getClick() == ClickType.RIGHT){
                    value += incrementer*2;

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 2);
                }else{
                    value += incrementer;

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 2);
                } 
            }
            updateMinus();
            updatePlus();
        });
    }

}
