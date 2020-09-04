package me.infinityz.minigame.gamemodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.types.Cutclean;
import me.infinityz.minigame.gamemodes.types.EnderRespawn;

public class GamemodeManager {
    UHC instance;
    private @Getter THashSet<IGamemode> gamemodesList = new THashSet<>();

    public GamemodeManager(UHC instance) {
        this.instance = instance;
        registerGamemode(new Cutclean());
        var ender = new EnderRespawn(instance);
        registerGamemode(ender);
        ender.enableScenario(instance);

    }

    public void registerGamemode(IGamemode gamemode) {
        gamemodesList.add(gamemode);
    }

    public Collection<IGamemode> getEnabledGamemodes() {
        final List<IGamemode> list = new ArrayList<>();
        gamemodesList.forEach(gamemode -> {
            if (gamemode.isEnabled())
                list.add(gamemode);
        });

        return list;

    }

    public String getEnabledGamemodesToString() {
        final StringBuilder sb = new StringBuilder();
        gamemodesList.forEach(all -> {
            if (all.isEnabled())
                sb.append(all.getName() + ", ");
        });
        return (sb.length() > 1 ? sb.toString().substring(sb.length(), sb.length() - 1) : "Vanilla") + ".";
    }

}