package me.infinityz.minigame.gui;

import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gui.types.EnabledScenarios;
import net.noobsters.kern.paper.guis.RapidInv;

public class GuiManager implements Listener {

    UHC instance;

    private @Getter @Setter EnabledScenarios enabledScenariosGui;

    public GuiManager(UHC instance) {
        this.instance = instance;

        enabledScenariosGui = new EnabledScenarios(new RapidInv(9*2, "Enabled Scenarios"));

    }

}