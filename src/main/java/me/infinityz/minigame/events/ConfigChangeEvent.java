package me.infinityz.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.infinityz.minigame.game.Game.ConfigType;

@RequiredArgsConstructor
public class ConfigChangeEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    private @Getter @Setter ConfigType configType;

    public ConfigChangeEvent(ConfigType configType) {
        this.configType = configType;
    }

}