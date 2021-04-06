package me.infinityz.minigame.gui.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class GameLoopGui extends CustomGui {

    UHC instance = UHC.getInstance();
    String permissionConfig = "uhc.config.cmd";

    SwitchGui pvpSwitch = new SwitchGui(new RapidInv(InventoryType.HOPPER, "PvP time"), 1);
    SwitchGui healSwitch = new SwitchGui(new RapidInv(InventoryType.HOPPER, "Heal time"), 1);
    SwitchGui borderTimeSwitch = new SwitchGui(new RapidInv(InventoryType.HOPPER, "Border time"), 5);
    SwitchGui borderSizeSwitch = new SwitchGui(new RapidInv(InventoryType.HOPPER, "Border size"), 100);
    SwitchGui borderCenterTimeSwitch = new SwitchGui(new RapidInv(InventoryType.HOPPER, "Border to center time"), 1);

    public GameLoopGui(RapidInv gui) {
        super(gui);

        pvpSwitch.setUpLimit(60);
        var sword = new ItemBuilder(Material.IRON_SWORD).name(ChatColor.YELLOW + "PvP time")
                .lore(ChatColor.GREEN + "Confirm").flags(ItemFlag.HIDE_ATTRIBUTES).build();
        pvpSwitch.getGui().setItem(2, sword, action -> {
            var player = (Player) action.getWhoClicked();
            if (instance.getGame().getPvpTime() != pvpSwitch.getValue()) {
                Bukkit.dispatchCommand(player, "gameloop pvptime " + pvpSwitch.getValue());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
            } else {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
            getGui().open(player);

        });

        healSwitch.setUpLimit(60);
        var heal = new ItemBuilder(Material.SPLASH_POTION).name(ChatColor.YELLOW + "Heal time")
                .meta(PotionMeta.class,
                        meta -> meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, false)))
                .flags(ItemFlag.HIDE_POTION_EFFECTS).lore(ChatColor.GREEN + "Confirm").build();

        healSwitch.getGui().setItem(2, heal, action -> {
            var player = (Player) action.getWhoClicked();
            if (instance.getGame().getHealTime() != healSwitch.getValue()) {
                Bukkit.dispatchCommand(player, "gameloop healtime " + healSwitch.getValue());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
            } else {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
            getGui().open(player);

        });

        borderSizeSwitch.setUpLimit(6000);
        var borderSize = new ItemBuilder(Material.BEDROCK).name(ChatColor.YELLOW + "Border size")
                .lore(ChatColor.GREEN + "Confirm").build();
        borderSizeSwitch.getGui().setItem(2, borderSize, action -> {
            var player = (Player) action.getWhoClicked();
            if (instance.getGame().getBorderSize() != borderSizeSwitch.getValue()) {
                Bukkit.dispatchCommand(player, "gameloop bordersize " + borderSizeSwitch.getValue());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
            } else {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
            getGui().open(player);

        });

        borderTimeSwitch.setUpLimit(60 * 3);
        var bordertime = new ItemBuilder(Material.CLOCK).name(ChatColor.YELLOW + "Border time")
                .lore(ChatColor.GREEN + "Confirm").build();
        borderTimeSwitch.getGui().setItem(2, bordertime, action -> {
            var player = (Player) action.getWhoClicked();
            if (instance.getGame().getBorderTime() != borderTimeSwitch.getValue()) {
                Bukkit.dispatchCommand(player, "gameloop bordertime " + borderTimeSwitch.getValue());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
            } else {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
            getGui().open(player);

        });

        borderCenterTimeSwitch.setUpLimit(6000);
        var bordercentertime = new ItemBuilder(Material.CLOCK).name(ChatColor.YELLOW + "Border to center time")
                .lore(ChatColor.GREEN + "Confirm").build();
        borderCenterTimeSwitch.getGui().setItem(2, bordercentertime, action -> {
            var player = (Player) action.getWhoClicked();
            if (instance.getGame().getBorderSize() != borderCenterTimeSwitch.getValue()) {
                Bukkit.dispatchCommand(player, "gameloop bordercentertime " + borderCenterTimeSwitch.getValue());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE, 1.0f, 1);
            } else {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
            getGui().open(player);

        });

        gui.setItem(17, new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.GREEN + "UHC Game").build(), action -> {
            var player = (Player) action.getWhoClicked();
            instance.getGuiManager().getMainGui().open((Player) action.getWhoClicked());
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
        });

        update();

    }

    @Override
    public void update() {
        updatePvpTime();
        updateHealTime();
        updateBorderSize();
        updateBorderTime();
        updateBorderCenterTime();

    }

    public void updatePvpTime() {
        var gui = getGui();
        var item = new ItemBuilder(Material.IRON_SWORD).name(ChatColor.YELLOW + "PvP time")
                .lore(ChatColor.WHITE + "At " + instance.getGame().getPvpTime() / 60 + " minutes")
                .flags(ItemFlag.HIDE_ATTRIBUTES).build();
        gui.setItem(0, item, action -> {
            var player = (Player) action.getWhoClicked();
            if (action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)) {

                pvpSwitch.setValue(instance.getGame().getPvpTime() / 60);
                pvpSwitch.update();
                pvpSwitch.open(player);

            } else {
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });
    }

    public void updateHealTime() {
        var gui = getGui();
        var item = new ItemBuilder(Material.SPLASH_POTION).name(ChatColor.YELLOW + "Heal time")
                .meta(PotionMeta.class,
                        meta -> meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, false)))
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .lore(ChatColor.WHITE + "At " + instance.getGame().getHealTime() / 60 + " minutes").build();
        gui.setItem(1, item, action -> {
            var player = (Player) action.getWhoClicked();
            if (action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)) {

                healSwitch.setValue(instance.getGame().getHealTime() / 60);
                healSwitch.update();
                healSwitch.open(player);

            } else {
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });
    }

    public void updateBorderSize() {
        var gui = getGui();
        var item = new ItemBuilder(Material.BEDROCK).name(ChatColor.YELLOW + "Border size")
                .lore(ChatColor.WHITE + "" + instance.getGame().getBorderSize() / 2 + " blocks of radius").build();
        gui.setItem(2, item, action -> {
            var player = (Player) action.getWhoClicked();
            if (action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)) {

                borderSizeSwitch.setValue(instance.getGame().getBorderSize());
                borderSizeSwitch.update();
                borderSizeSwitch.open(player);

            } else {
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });
    }

    public void updateBorderTime() {
        var gui = getGui();
        var item = new ItemBuilder(Material.CLOCK).name(ChatColor.YELLOW + "Border time")
                .lore(ChatColor.WHITE + "At " + instance.getGame().getBorderTime() / 60 + " minutes").build();
        gui.setItem(3, item, action -> {
            var player = (Player) action.getWhoClicked();
            if (action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)) {

                borderTimeSwitch.setValue(instance.getGame().getBorderTime() / 60);
                borderTimeSwitch.update();
                borderTimeSwitch.open(player);

            } else {
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });
    }

    public void updateBorderCenterTime() {
        var gui = getGui();
        var item = new ItemBuilder(Material.CLOCK).name(ChatColor.YELLOW + "Border to center time")
                .lore(ChatColor.WHITE + "At " + instance.getGame().getBorderTime() / 60 + " minutes").build();
        gui.setItem(4, item, action -> {
            var player = (Player) action.getWhoClicked();
            if (action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)) {

                borderCenterTimeSwitch.setValue(instance.getGame().getBorderCenterTime() / 60);
                borderCenterTimeSwitch.update();
                borderCenterTimeSwitch.open(player);

            } else {
                instance.getGuiManager().getMainGui().open(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, SoundCategory.VOICE, 1.0f, 1.0f);
            }
        });
    }

}