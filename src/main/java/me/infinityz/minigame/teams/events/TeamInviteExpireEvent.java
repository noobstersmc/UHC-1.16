package me.infinityz.minigame.teams.events;

import com.github.benmanes.caffeine.cache.RemovalCause;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.teams.objects.TeamInvite;

@RequiredArgsConstructor(staticName = "of")
public class TeamInviteExpireEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    /*
     * Custom data, use @NonNull for the constructor
     */
    private @NonNull @Getter TeamInvite invite;
    private @NonNull @Getter RemovalCause removalCause;

    public TeamInviteExpireEvent(TeamInvite invite, RemovalCause removalCause, Boolean async) {
        super(true);
        this.invite = invite;
        this.removalCause = removalCause;
    }

}