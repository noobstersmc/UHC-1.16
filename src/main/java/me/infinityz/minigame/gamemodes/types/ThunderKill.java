package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.UHCPlayerDequalificationEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class ThunderKill extends IGamemode implements Listener {
    private UHC instance;
    private boolean thunder = false;

    public ThunderKill(UHC instance) {
        super("ThunderKill", "Every time a player dies weather changes between day and storm.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onPlayerDeath(UHCPlayerDequalificationEvent e){
        if(!thunder){
            Bukkit.getWorlds().forEach(worlds ->{
                
            });
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:world run weather thunder");
            thunder = true;
        } else{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:world run weather clear");
            thunder = false;
        }
    }

}