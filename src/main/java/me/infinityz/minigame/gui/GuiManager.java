package me.infinityz.minigame.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.events.CustomRecipeAddedEvent;
import me.infinityz.minigame.crafting.events.CustomRecipeRemovedEvent;
import me.infinityz.minigame.events.ConfigChangeEvent;
import me.infinityz.minigame.gamemodes.events.GamemodeDisabledEvent;
import me.infinityz.minigame.gamemodes.events.GamemodeEnabledEvent;
import me.infinityz.minigame.gui.types.ConfigGui;
import me.infinityz.minigame.gui.types.EnabledCrafting;
import me.infinityz.minigame.gui.types.EnabledScenarios;
import net.noobsters.kern.paper.guis.RapidInv;

public class GuiManager implements Listener {

    UHC instance;

    private @Getter @Setter EnabledScenarios enabledScenariosGui = new EnabledScenarios(new RapidInv(9 * 2, "Scenarios"));
    private @Getter @Setter EnabledCrafting enabledCraftingGui = new EnabledCrafting(new RapidInv(9 * 2, "Crafting"));
    private @Getter @Setter ConfigGui configGui = new ConfigGui(new RapidInv(9 * 2, "Config"));


    public GuiManager(UHC instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);

    }

    @EventHandler
    public void scenarioEnable(GamemodeEnabledEvent e) {
        enabledScenariosGui.update();
    }

    @EventHandler
    public void scenarioDisable(GamemodeDisabledEvent e) {
        enabledScenariosGui.update();
    }

    @EventHandler
    public void craftAdded(CustomRecipeAddedEvent e){
        enabledCraftingGui.update();
    }

    @EventHandler
    public void craftRemoved(CustomRecipeRemovedEvent e){
        enabledCraftingGui.update();
    }

    @EventHandler
    public void configChange(ConfigChangeEvent e){
        switch (e.getConfigType()) {
            
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
        
            default:
                break;
        }
    }

}