package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.GameStartedEvent;
import me.noobsters.minigame.events.PlayerJoinedLateEvent;
import me.noobsters.minigame.gamemodes.IGamemode;

import org.bukkit.attribute.Attribute;

public class DoubleLifeBar extends IGamemode implements Listener {
    private UHC instance;

    public DoubleLifeBar(UHC instance) {
        super("DoubleLifeBar", "All players will have double life bar.", Material.GLISTERING_MELON_SLICE);
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onStart(GameStartedEvent e) {

        Bukkit.getScheduler().runTask(instance, ()->{
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                players.setHealth(40);
            });

        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinLate(PlayerJoinedLateEvent e){
        var player = e.getPlayer();
        Bukkit.getScheduler().runTask(instance, ()->{
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
            player.setHealth(40);

        });
    }

}