package me.infinityz.minigame.crafting.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.crafting.CustomRecipe;

@RequiredArgsConstructor
public class CustomRecipeAddedEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    private @Getter @NonNull CustomRecipe customRecipe;

    public CustomRecipeAddedEvent(CustomRecipe customRecipe, boolean async) {
        super(async);
        this.customRecipe = customRecipe;
    }

}