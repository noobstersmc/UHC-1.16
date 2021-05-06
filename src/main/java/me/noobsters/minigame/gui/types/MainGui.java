package me.noobsters.minigame.gui.types;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.game.Game.GameInfo;
import me.noobsters.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class MainGui extends CustomGui {

    UHC instance = UHC.getInstance();

    private @Getter @Setter EnabledScenarios enabledScenariosGui = new EnabledScenarios(
            new RapidInv(9 * 2, "Scenarios"));
    private @Getter @Setter EnabledCrafting enabledCraftingGui = new EnabledCrafting(new RapidInv(9 * 2, "Crafting"));
    private @Getter @Setter ConfigGui configGui = new ConfigGui(new RapidInv(9 * 2, "Settings"));
    private @Getter @Setter GameLoopGui gameLoopGui = new GameLoopGui(new RapidInv(9 * 2, "Game Loop"));
    private @Getter @Setter List<RapidInv> scenarioPages = new ArrayList<>();
    private @Getter @Setter RapidInv toggleCraftingGui = new RapidInv(9 * 2, "Crafting");
    String permissionConfig = "uhc.config.cmd";
    String color1 = "" + ChatColor.of("#eb9c4c");
    DecimalFormat numberFormat = new DecimalFormat("#0.0");

    public MainGui(RapidInv gui) {
        super(gui);

        //scenario toggle gui
        var listCount = instance.getGamemodeManager().getGamemodesList().size();

        int count = (int) listCount / 52;
        float countFloat = (float) listCount / 52;
        if (countFloat-count != 0) {
            
            count++;
        }

        for (int i = 0; i < count; i++) {
            var page = new RapidInv(9 * 6, "Scenarios");

            page.setItem(53, new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.GREEN + "UHC Game").build(),
                    action -> {
                        var player = (Player) action.getWhoClicked();
                        instance.getGuiManager().getMainGui().open((Player) action.getWhoClicked());
                        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE,
                                SoundCategory.VOICE, 1.0f, 1.0f);
                    });

            scenarioPages.add(page);
        }

        for (int i = 0; i < scenarioPages.size(); i++) {
            var page = scenarioPages.get(i);
            var arrow = new ItemBuilder(Material.TIPPED_ARROW).name(ChatColor.GREEN + "Next page")
                    .flags(ItemFlag.HIDE_POTION_EFFECTS)
                    .meta(PotionMeta.class, meta -> meta.setColor(Color.fromRGB(0, 0, 0))).build();

                    
            var nextPage = scenarioPages.get(0);

            if(i+1 >= scenarioPages.size()) nextPage = scenarioPages.get(0);
            else nextPage = scenarioPages.get(i+1);
            

            final var next = nextPage;

            page.setItem(52, arrow, action -> {
                var player = (Player) action.getWhoClicked();
                next.open(player);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE,
                        1.0f, 0.1f);
            });

        }

        //crafting
        toggleCraftingGui.setItem(17, new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.GREEN + "UHC Game").build(),
        action -> {
            var player = (Player) action.getWhoClicked();
            instance.getGuiManager().getMainGui().open((Player) action.getWhoClicked());
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE,
                    SoundCategory.VOICE, 1.0f, 1.0f);
        });

        update();
    }

    @Override
    public void update() {
        updateGameLoop();
        updateCrafting();
        updateGame();
        updateSettings();
        updateInfo();
        updateScenarioPages();
        updateToggleCraftingGui();
    }

    public void updateToggleCraftingGui(){
        var gui = toggleCraftingGui;
        var crafts = instance.getCraftingManager().getAllRecipes();
        var i = 0;
        for (var craft : crafts.entrySet()) {
            var result = craft.getValue().getRecipe().getResult().getType();
            var item = new ItemBuilder(result).name(ChatColor.YELLOW + craft.getKey());

            if(craft.getValue().isEnabled()) item.enchant(Enchantment.MENDING).flags(ItemFlag.HIDE_ENCHANTS);

            item.addLore(craft.getValue().isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled");

            gui.setItem(i, item.build(), action->{
                var player = (Player) action.getWhoClicked();
                Bukkit.dispatchCommand(player, "crafting toggle " + craft.getKey());
            });
            i++;
        }
        
    }

    public void updateScenarioPages() {

        var igamemodes = instance.getGamemodeManager().getGamemodesList().stream().collect(Collectors.toList());
        var gm = 0;
        for (int i = 0; i < scenarioPages.size(); i++) {

            var page = scenarioPages.get(i);
            for (int slot = 0; slot < 52; slot++) {

                if(gm >= igamemodes.size()) return;

                var gamemode = igamemodes.get(gm);
                
                var icon = gamemode.getAsIcon();
                if (gamemode.isEnabled())
                    icon.enchant(Enchantment.MENDING).flags(ItemFlag.HIDE_ENCHANTS);

                icon.addLore(gamemode.isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled");
                
                page.setItem(slot, icon.build(), action -> {
                    var player = (Player) action.getWhoClicked();
                    Bukkit.dispatchCommand(player, "scenario toggle " + gamemode.getName());
                    
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f,
                            0.1f);
                });
                gm++;
            }
        }
    }

    public void updateGameLoop() {
        var gui = getGui();
        var game = instance.getGame();
        var gameloop = new ItemBuilder(Material.IRON_SWORD).name(ChatColor.DARK_AQUA + "Game Loop")
                .flags(ItemFlag.HIDE_ATTRIBUTES);

        gameloop.addLore(color1 + "PvP time: " + ChatColor.WHITE + game.getPvpTime() / 60 + " minutes");
        gameloop.addLore(color1 + "Heal time: " + ChatColor.WHITE + game.getHealTime() / 60 + " minutes");
        gameloop.addLore(color1 + "Border time: " + ChatColor.WHITE + game.getBorderTime() / 60 + " minutes");
        gameloop.addLore(color1 + "Border size: " + ChatColor.WHITE + game.getBorderSize() / 2 + " blocks of radius");
        gameloop.addLore(
                color1 + "Border to center time: " + ChatColor.WHITE + game.getBorderCenterTime() / 60 + " minutes");
        gui.setItem(0, gameloop.build(), action -> {
            var player = (Player) action.getWhoClicked();
            instance.getGuiManager().getMainGui().getGameLoopGui().open(player);
        });
    }

    public void updateCrafting() {
        var gui = getGui();
        var crafting = new ItemBuilder(Material.CRAFTING_TABLE).name(ChatColor.DARK_AQUA + "Crafting");

        instance.getCraftingManager().getAllRecipes().entrySet().stream()
                .filter(recipe -> recipe.getValue().isEnabled()).forEach(craft -> {
                    crafting.addLore(ChatColor.WHITE + "- " + craft.getKey());
                });

        gui.setItem(1, crafting.build(), action -> {
            var player = (Player) action.getWhoClicked();
            if (action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)) {
                toggleCraftingGui.open(player);
            } else {
                enabledCraftingGui.open(player);
            }
        });
    }

    public void updateGame() {
        var gui = getGui();
        var uhc = new ItemBuilder(Material.TOTEM_OF_UNDYING).name(ChatColor.DARK_AQUA + "UHC Game");

        uhc.addLore(color1 + "Mode: " + ChatColor.WHITE + (instance.getTeamManger().getTeamSize() == 1 ? "FFA"
                : "Teams of " + instance.getTeamManger().getTeamSize()));
        var gamemodes = instance.getGamemodeManager().getEnabledGamemodes();
        if (gamemodes.isEmpty()) {
            uhc.addLore(ChatColor.WHITE + "- " + "Vanilla+");
        } else {
            gamemodes.forEach(gamemode -> {
                uhc.addLore(ChatColor.WHITE + "- " + gamemode.getName());
            });
        }

        gui.setItem(2, uhc.build(), action -> {
            var player = (Player) action.getWhoClicked();
            if (action.getClick() == ClickType.RIGHT && player.hasPermission(permissionConfig)) {
                scenarioPages.get(0).open(player);
            } else {
                enabledScenariosGui.open(player);
            }
        });
    }

    public void updateSettings() {
        var gui = getGui();
        var settings = new ItemBuilder(Material.ANVIL).name(ChatColor.DARK_AQUA + "Settings");
        var game = instance.getGame();

        settings.addLore(color1 + "Apple rate: " + ChatColor.WHITE + numberFormat.format(game.getAppleRate()) + "%");
        settings.addLore(color1 + "Flint rate: " + ChatColor.WHITE + numberFormat.format(game.getFlintRate()) + "%");
        settings.addLore(color1 + "Trident: " + ChatColor.WHITE + (game.isTrident() ? "100% drop" : "Vanilla"));
        settings.addLore(color1 + "Nether: " + (game.isNether() ? ChatColor.GREEN : ChatColor.RED) + game.isNether());
        settings.addLore(color1 + "Nerfed strength 50%: " + (game.isStrengthNerf() ? ChatColor.GREEN : ChatColor.RED)
                + game.isStrengthNerf());
        settings.addLore(color1 + "Nerfed bed explosion: " + (game.isBedsNerf() ? ChatColor.GREEN : ChatColor.RED)
                + game.isBedsNerf());

        if (game.isAdvancements())
            settings.addLore(color1 + "Advancements: " + (game.isAdvancements() ? ChatColor.GREEN : ChatColor.RED)
                    + game.isAdvancements());
        if (!game.isHorses())
            settings.addLore(
                    color1 + "Horses: " + (game.isHorses() ? ChatColor.GREEN : ChatColor.RED) + game.isHorses());
        if (!game.isBeds())
            settings.addLore(color1 + "Beds: " + (game.isBeds() ? ChatColor.GREEN : ChatColor.RED) + game.isBeds());
        if (!game.isStrength())
            settings.addLore(
                    color1 + "Strength: " + (game.isStrength() ? ChatColor.GREEN : ChatColor.RED) + game.isStrength());
        if (!game.isPotions())
            settings.addLore(
                    color1 + "Potions: " + (game.isPotions() ? ChatColor.GREEN : ChatColor.RED) + game.isPotions());
        if (!game.isItemsBurn())
            settings.addLore(color1 + "Items burn: " + (game.isItemsBurn() ? ChatColor.GREEN : ChatColor.RED)
                    + game.isItemsBurn());
        if (!game.isTears())
            settings.addLore(
                    color1 + "Ghast tears: " + (game.isTears() ? ChatColor.GREEN : ChatColor.RED) + game.isTears());
        if (!game.isTrades())
            settings.addLore(
                    color1 + "Trades: " + (game.isTrades() ? ChatColor.GREEN : ChatColor.RED) + game.isTrades());

        gui.setItem(3, settings.build(), action -> {
            configGui.open((Player) action.getWhoClicked());
        });
    }

    public void updateInfo() {
        var gui = getGui();
        var info = new ItemBuilder(Material.LODESTONE).name(ChatColor.DARK_AQUA + "Information");
        info.addLore(color1 + "Host: " + ChatColor.WHITE + instance.getGame().getHostname());
        info.addLore(color1 + "Slots: " + ChatColor.WHITE + instance.getGame().getUhcslots());
        info.addLore(color1 + "Game: " + ChatColor.WHITE + (instance.getGame().isPrivateGame() ? "Private" : instance.getGame().getGameInfo().toString()));
        info.addLore(color1 + "Stats: " + ChatColor.WHITE + (instance.getGame().getGameInfo() == GameInfo.OFFICIAL ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));

        gui.setItem(4, info.build(), action -> {
            var player = (Player) action.getWhoClicked();
            if (player.hasPermission(permissionConfig)) {

                var confirmationGui = new ConfirmationGui(new RapidInv(InventoryType.HOPPER, "Private Game"),

                        confirm -> {
                            Bukkit.dispatchCommand(player, "config privategame " + !instance.getGame().isPrivateGame());
                            gui.open(player);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.VOICE,
                                    1.0f, 1);
                        }, deny -> {
                            gui.open(player);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                        });

                confirmationGui.open(player);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
            }
        });
    }

}
