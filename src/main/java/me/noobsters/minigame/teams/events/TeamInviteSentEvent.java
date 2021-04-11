package me.noobsters.minigame.teams.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.teams.objects.Team;
import me.noobsters.minigame.teams.objects.TeamInvite;

@RequiredArgsConstructor
public class TeamInviteSentEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    /*
     * Custom data, use @NonNull for the constructor
     */
    private @NonNull @Getter Team team;
    private @NonNull @Getter TeamInvite invite;

}