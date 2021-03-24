package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.gamemodes.interfaces.ScenarioPack;

public class MetaGame extends IGamemode implements ScenarioPack, Listener {
    private ArrayList<IGamemode> gamemodes = new ArrayList<>();
    private UHC instance;

    public MetaGame(UHC instance) {
        super("MetaGame", "CutClean, Timber & HasteyBoys.");
        this.instance = instance;
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            var manager = instance.getGamemodeManager();
            gamemodes.add(manager.getScenario(Cutclean.class));
            gamemodes.add(manager.getScenario(HasteyBoys.class));
            gamemodes.add(manager.getScenario(Timber.class));

        }, 10);

    }

    @Override
    public ArrayList<IGamemode> getGamemodes() {
        return gamemodes;
    }

    @Override
    public String getDescription() {
        String addedDescription = "";
        for (var scen : gamemodes) {
            if (!scen.isEnabled()) {
                addedDescription = addedDescription + " " + scen.getName() + " disabled";
            }
        }
        return super.getDescription() + addedDescription;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        gamemodes.forEach(scenarios -> {
            scenarios.callEnable();
        });

        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;

        gamemodes.forEach(scenarios -> {
            scenarios.callDisable();
        });

        instance.getListenerManager().unregisterListener(this);

        setEnabled(false);
        return true;
    }


}
