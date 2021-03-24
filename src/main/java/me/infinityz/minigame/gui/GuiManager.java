package me.infinityz.minigame.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.events.GamemodeDisabledEvent;
import me.infinityz.minigame.gamemodes.events.GamemodeEnabledEvent;
import me.infinityz.minigame.gui.types.EnabledScenarios;
import net.noobsters.kern.paper.guis.RapidInv;

public class GuiManager implements Listener {

    UHC instance;

    private @Getter @Setter EnabledScenarios enabledScenariosGui;

    public GuiManager(UHC instance) {
        this.instance = instance;

        enabledScenariosGui = new EnabledScenarios(new RapidInv(9 * 2, "Enabled Scenarios"));
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

}