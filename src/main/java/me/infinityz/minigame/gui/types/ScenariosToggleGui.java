package me.infinityz.minigame.gui.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class ScenariosToggleGui extends CustomGui {

    UHC instance = UHC.getInstance();
    public @Getter @Setter List<IGamemode> gamemodes = new ArrayList<>();

    public ScenariosToggleGui(RapidInv gui) {
        super(gui);

        gui.setItem(0, new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.GREEN + "UHC Game").build(), action -> {
            var player = (Player) action.getWhoClicked();
            instance.getGuiManager().getMainGui().open((Player) action.getWhoClicked());
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
        });
    }

    @Override
    public void update() {

    }

    public void updateScenario(int slot) {
        var gui = getGui();
        var gamemode = gamemodes.get(slot);
        var icon = gamemode.getAsIcon();
        if (gamemode.isEnabled())
            icon.enchant(Enchantment.MENDING).flags(ItemFlag.HIDE_ENCHANTS);
        gui.setItem(slot, icon.build(), action -> {
            var player = (Player) action.getWhoClicked();
            Bukkit.dispatchCommand(player, "scenario toggle " + gamemode.getName());
            updateScenario(slot);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
        });
    }

    public void addScenario(IGamemode gamemode){
        var gui = getGui();
        var icon = gamemode.getAsIcon();
        if (gamemode.isEnabled())
            icon.enchant(Enchantment.MENDING).flags(ItemFlag.HIDE_ENCHANTS);
        var slot = gamemodes.size();
        gui.setItem(slot, icon.build(), action -> {
            var player = (Player) action.getWhoClicked();
            Bukkit.dispatchCommand(player, "scenario toggle " + gamemode.getName());
            updateScenario(slot);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
        });
        gamemodes.add(gamemode);
    }

}