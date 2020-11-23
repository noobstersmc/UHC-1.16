package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import org.bukkit.attribute.Attribute;

public class DoubleLifeBar extends IGamemode implements Listener {
    private UHC instance;

    public DoubleLifeBar(UHC instance) {
        super("DoubleLifeBar", "Double life bar.");
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

        Bukkit.getOnlinePlayers().forEach(players -> {
            players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);      
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinLate(PlayerJoinedLateEvent e){
        var player = e.getPlayer();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
    }

}