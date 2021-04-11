package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.UHCPlayerDequalificationEvent;
import me.noobsters.minigame.gamemodes.IGamemode;

public class PermaGlow extends IGamemode implements Listener {
    private UHC instance;
    private boolean glow = false;

    public PermaGlow(UHC instance) {
        super("PermaGlow", "Every time a player dies, glowing changes.", Material.SPECTRAL_ARROW);
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
        if(!glow){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @a minecraft:glowing 10000 10 true");
            glow = true;
        } else{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect clear @a minecraft:glowing");
            glow = false;
        }
    }

}