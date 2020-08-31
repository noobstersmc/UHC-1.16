package me.infinityz.minigame.gamemodes;

import java.util.Optional;

import org.bukkit.event.Listener;

import co.aikar.commands.BaseCommand;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;

/*
* As of right now, there is really no reason to use abstact here.
*/
public abstract class IGamemode {
    private @Getter @Setter String name;
    private @Getter @Setter String description;
    private @Getter @Setter boolean enabled = false;
    private @Getter @Setter Optional<BaseCommand> command;
    private @Getter @Setter Optional<Listener> listener;

    public IGamemode(String name, String description, BaseCommand baseCommand, Listener listener) {
        this.name = name;
        this.description = description;
        this.command = Optional.ofNullable(baseCommand);
        this.listener = Optional.ofNullable(listener);
    }

    public IGamemode(String name, String description) {
        this(name, description, null, null);
    }

    public boolean enableScenario(UHC instance) {
        if (enabled)
            return false;
        if (listener.isPresent())
            instance.getListenerManager().registerListener(listener.get());
        if (command.isPresent())
            instance.getCommandManager().registerCommand(command.get());

        return true;
    }

    public boolean disableScenario(UHC instance) {
        if (!enabled)
            return false;
        if (listener.isPresent())
            instance.getListenerManager().unregisterListener(listener.get());
        if (command.isPresent())
            instance.getCommandManager().unregisterCommand(command.get());

        return true;
    }

}