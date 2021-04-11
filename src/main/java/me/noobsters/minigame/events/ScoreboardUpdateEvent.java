package me.noobsters.minigame.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import me.noobsters.minigame.scoreboard.IScoreboard;

public class ScoreboardUpdateEvent extends Event implements Cancellable{
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private @Getter @Setter boolean cancelled = false;
    /*
     * Custom data
     */
    private @Getter @Setter IScoreboard scoreboard;
    private @Getter @Setter String[] lines;

    public ScoreboardUpdateEvent(IScoreboard scoreboard, boolean async, String... lines) {
        super(async);
        this.lines = lines;
        this.scoreboard = scoreboard;
    }

    public ScoreboardUpdateEvent(IScoreboard scoreboard, String... lines) {
        this(scoreboard, false, lines);
    }

    public void setLinesArray(String... lines){ 
        this.lines = lines;
    }


}
