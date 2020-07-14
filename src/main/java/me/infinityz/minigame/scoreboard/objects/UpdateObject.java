package me.infinityz.minigame.scoreboard.objects;

public class UpdateObject {
    private String text;
    private int line;

    public UpdateObject(String text, int line){
        this.text  = text;
        this.line = line;
    }

    public int getLine() {
        return line;
    }
    public String getText() {
        return text;
    }
    
}