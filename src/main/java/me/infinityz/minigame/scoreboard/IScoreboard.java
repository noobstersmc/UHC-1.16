package me.infinityz.minigame.scoreboard;

import org.bukkit.entity.Player;

import fr.mrmicky.fastboard.FastBoard;

public abstract class IScoreboard extends FastBoard {

    public IScoreboard(Player player) {
        super(player);
    }

    public abstract void update();
    
}