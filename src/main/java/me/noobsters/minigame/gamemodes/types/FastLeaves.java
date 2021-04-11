package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;

import me.noobsters.minigame.gamemodes.IGamemode;

public class FastLeaves extends IGamemode {

    public FastLeaves() {
        super("FastLeaves", "Leaves decay faster.", Material.OAK_LEAVES);
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