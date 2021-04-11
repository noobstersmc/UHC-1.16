package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.GameTickEvent;
import me.noobsters.minigame.gamemodes.IGamemode;

import org.bukkit.attribute.Attribute;

public class MeetupDoubleLifeBar extends IGamemode implements Listener {
    private UHC instance;

    public MeetupDoubleLifeBar(UHC instance) {
        super("MeetupDoubleLifeBar", "All players will have double life bar at meetup.", Material.GLISTERING_MELON_SLICE);
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
    public void onJoin(PlayerJoinEvent e){
        var player = e.getPlayer();
        var game = instance.getGame();
        if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() < 40 && game.getGameTime() > game.getBorderTime()){
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
        }
    }   

    @EventHandler
    public void onStart(GameTickEvent e) {
        if (e.getSecond() == instance.getGame().getBorderTime()) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                players.setHealth(players.getHealth()+20.0);
            });
        }

    }

    

}