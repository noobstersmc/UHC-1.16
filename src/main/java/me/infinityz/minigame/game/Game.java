package me.infinityz.minigame.game;

import java.util.UUID;

import lombok.Data;

@Data
public class Game {
    private long startTime;
    private int gameTime = 0;
    private boolean pvp = false;
    private boolean globalMute = false;
    private boolean nether = true;
    private boolean end = true;
    private UUID gameID = UUID.randomUUID();

}