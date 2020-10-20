package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

import me.infinityz.minigame.gamemodes.IGamemode;

public class FastLeavesDecay extends IGamemode {

    public FastLeavesDecay() {
        super("FastLeavesDecay", "Leaves decay faster.");
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        Bukkit.getWorlds().forEach(this::changeTickSpeed);

        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;

        Bukkit.getWorlds().forEach(this::defaultTickSpeed);
        setEnabled(false);
        return true;
    }

    void changeTickSpeed(World world) {
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 150);
    }

    void defaultTickSpeed(World world) {
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
    }

}