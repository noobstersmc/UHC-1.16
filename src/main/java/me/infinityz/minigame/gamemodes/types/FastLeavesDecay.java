package me.infinityz.minigame.gamemodes.types;

import org.bukkit.event.Listener;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class FastLeavesDecay extends IGamemode implements Listener {
    private UHC instance;

    public FastLeavesDecay(UHC instance) {
        super("FastLeavesDecay", "Leaves decay faster.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    public void onLeaveDecay(){

    }

}