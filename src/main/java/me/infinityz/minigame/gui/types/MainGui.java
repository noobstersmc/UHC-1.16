package me.infinityz.minigame.gui.types;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gui.CustomGui;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class MainGui extends CustomGui {

    UHC instance = UHC.getInstance();

    private @Getter @Setter EnabledScenarios enabledScenariosGui = new EnabledScenarios(
            new RapidInv(9 * 2, "Scenarios"));
    private @Getter @Setter EnabledCrafting enabledCraftingGui = new EnabledCrafting(new RapidInv(9 * 2, "Crafting"));
    private @Getter @Setter ConfigGui configGui = new ConfigGui(new RapidInv(9 * 2, "Settings"));
    private @Getter @Setter GameLoopGui gameLoopGui = new GameLoopGui(new RapidInv(9 * 2, "Game Loop"));
    private @Getter @Setter List<ScenariosToggleGui> scenarioPages = new ArrayList<>();
    String permissionConfig = "uhc.config.cmd";
    String color1 = "" + ChatColor.of("#eb9c4c");
    DecimalFormat numberFormat = new DecimalFormat("#0.0");

    public MainGui(RapidInv gui) {
        super(gui);

        var listCount = instance.getGamemodeManager().getGamemodesList().size();
        if (listCount <= 53) {
            scenarioPages.add(new ScenariosToggleGui(new RapidInv(9 * 2, "Scenarios")));
        } else {
            var count = (int) listCount / 53;
            if (listCount % 53 == 0) {
                for (int i = 0; i < count; i++) {
                    scenarioPages.add(new ScenariosToggleGui(new RapidInv(9 * 2, "Scenarios")));
                }
            } else {
                for (int i = 0; i < count + 1; i++) {
                    scenarioPages.add(new ScenariosToggleGui(new RapidInv(9 * 2, "Scenarios")));
                }
            }
        }

        update();
    }

    @Override
    public void update() {
        updateGameLoop();
        updateCrafting();
        updateGame();
        updateSettings();
        updateInfo();
    }

    public void setUpScenarioPages() {

        var igamemodes = instance.getGamemodeManager().getGamemodesList().stream().collect(Collectors.toList());

        if (scenarioPages.size() == 1) {
            var page = scenarioPages.get(0).getRapidInv().getInventory().getContents();
            var gui = scenarioPages.get(0);
            for (int i = 1; i < igamemodes.size(); i++) {
                var gamemode = igamemodes.get(i);
                gui.addScenario(gamemode);
            }
        }else {
            var gamemodeCount = 0;
            for (int i = 0; i < scenarioPages.size(); i++) {
                var page = scenarioPages.get(i).getRapidInv().getInventory().getContents();
                var gui = scenarioPages.get(i);

                var arrow = new ItemBuilder(Material.TIPPED_ARROW).name(ChatColor.GREEN + "Next page").flags(ItemFlag.HIDE_POTION_EFFECTS)
                .meta(PotionMeta.class, meta -> meta.setColor(Color.fromRGB(0, 0, 0))).build();

                final var n = i;
                gui.getRapidInv().setItem(53, arrow, action ->{
                    var player = (Player) action.getWhoClicked();
                    if(n == scenarioPages.size()){
                        var nextPage = scenarioPages.get(0);
                        nextPage.open(player);
                        
                    }else{
                        var nextPage = scenarioPages.get(n+1).getRapidInv();
                        nextPage.open(player);
                        
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.VOICE, 1.0f, 0.1f);
                });

                for (int j = 1; j < 53; j++) {
                    //gui.addScenario(igamemodes.get(gamemodeCount));
                    gamemodeCount++;
                }
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
            if (player.hasPermission(permissionConfig)) {
                instance.getGuiManager().getMainGui().getGameLoopGui().open(player);
            }
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
            enabledCraftingGui.open((Player) action.getWhoClicked());
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
        info.addLore(color1 + "Game: " + ChatColor.WHITE + (instance.getGame().isPrivateGame() ? "Private" : "Public"));
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
