package me.noobsters.minigame.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

import lombok.Getter;
import lombok.Setter;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.crafting.events.CustomRecipeAddedEvent;
import me.noobsters.minigame.crafting.events.CustomRecipeRemovedEvent;
import me.noobsters.minigame.events.ConfigChangeEvent;
import me.noobsters.minigame.gamemodes.events.GamemodeDisabledEvent;
import me.noobsters.minigame.gamemodes.events.GamemodeEnabledEvent;
import me.noobsters.minigame.gui.types.MainGui;
import net.noobsters.kern.paper.guis.RapidInv;

public class GuiManager implements Listener {

    UHC instance;

    private @Getter @Setter MainGui mainGui = new MainGui(new RapidInv(InventoryType.HOPPER, "UHC Game"));

    public GuiManager(UHC instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);

    }

    @EventHandler
    public void scenarioEnable(GamemodeEnabledEvent e) {
        mainGui.getEnabledScenariosGui().update();
        mainGui.updateGame();
        mainGui.updateScenarioPages();
    }

    @EventHandler
    public void scenarioDisable(GamemodeDisabledEvent e) {
        mainGui.getEnabledScenariosGui().update();
        mainGui.updateGame();
        mainGui.updateScenarioPages();
    }

    @EventHandler
    public void craftAdded(CustomRecipeAddedEvent e){
        mainGui.getEnabledCraftingGui().update();
        mainGui.updateCrafting();
        mainGui.updateToggleCraftingGui();
    }

    @EventHandler
    public void craftRemoved(CustomRecipeRemovedEvent e){
        mainGui.getEnabledCraftingGui().update();
        mainGui.updateCrafting();
        mainGui.updateToggleCraftingGui();
    }

    @EventHandler
    public void configChange(ConfigChangeEvent e){
        mainGui.updateSettings();
        mainGui.updateGameLoop();
        var configGui = mainGui.getConfigGui();
        var gameLoopGui = mainGui.getGameLoopGui();
        switch (e.getConfigType()) {

            case PVP_TIME: gameLoopGui.updatePvpTime();
                break;
            case HEAL_TIME: gameLoopGui.updateHealTime();
                break;
            case BORDER_TIME: gameLoopGui.updateBorderTime();
                break;
            case BORDER_SIZE: gameLoopGui.updateBorderSize();
                break;
            case BORDER_CENTER_TIME: gameLoopGui.updateBorderCenterTime();
                break;
            case TEAM_SIZE: mainGui.updateGame();
                break;
            case SLOTS:
            case HOSTNAME:
            case GAME: mainGui.updateInfo();
                break;
            case APPLE_RATE: configGui.updateAppleRate();
                break;
            case FLINT_RATE: configGui.updateFlintRate();
                break;
            case NETHER: configGui.updateNether();
                break;
            case ADVANCEMENTS: configGui.updateAdvancements();
                break;
            case HORSES: configGui.updateHorses();
                break;
            case BEDS: configGui.updateBeds();
                break;
            case BEDS_NERF: configGui.updateBedsNerf();
                break;
            case POTIONS: configGui.updatePotions();
                break;
            case POTIONS_TIER: configGui.updatePotionsTier();
                break;
            case STRENGTH: configGui.updateStrength();
                break;
            case STRENGTH_NERF: configGui.updateStrengthNerf();
                break;
            case TRADES: configGui.updateTrades();
                break;
            case ITEMS_BURN: configGui.updateItemsBurn();
                break;
            case TRIDENT: configGui.updateTrident();
                break;
            case TEARS: configGui.updateTears();
                break;
            case COBWEB: configGui.updateCobWeb();
                break;
        
            default:
                break;
        }
    }

}