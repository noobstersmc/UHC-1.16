package me.infinityz.minigame.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.events.CustomRecipeAddedEvent;
import me.infinityz.minigame.crafting.events.CustomRecipeRemovedEvent;
import me.infinityz.minigame.gamemodes.events.GamemodeDisabledEvent;
import me.infinityz.minigame.gamemodes.events.GamemodeEnabledEvent;
import me.infinityz.minigame.gui.types.EnabledCrafting;
import me.infinityz.minigame.gui.types.EnabledScenarios;
import net.noobsters.kern.paper.guis.RapidInv;

public class GuiManager implements Listener {

    UHC instance;

    private @Getter @Setter EnabledScenarios enabledScenariosGui;
    private @Getter @Setter EnabledCrafting enabledCraftingGui;

    public GuiManager(UHC instance) {
        this.instance = instance;

        enabledScenariosGui = new EnabledScenarios(new RapidInv(9 * 2, "Enabled Scenarios"));
        enabledCraftingGui = new EnabledCrafting(new RapidInv(9 * 2, "Enabled Crafting"));
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

}