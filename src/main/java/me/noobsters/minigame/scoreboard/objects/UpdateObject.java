package me.noobsters.minigame.scoreboard.objects;

//DEPRECATED
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