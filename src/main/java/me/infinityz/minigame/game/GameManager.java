package me.infinityz.minigame.game;

import me.infinityz.minigame.UHC;

public class GameManager {

    UHC instance;
    private Game game;

    public GameManager(UHC instance) {
        this.instance = instance;
    }
    public void sendData(){
        instance.getCondorManager().writeExpirableData(game.getGameID(), game.toString(), 10);
    }

    public boolean setGame(Game game) {
        if (this.game != null)//Don't allow it to be overwritten
            return false;
        this.game = game;
        return true;
    }

    public Game getGame() {
        return game;
    }

}