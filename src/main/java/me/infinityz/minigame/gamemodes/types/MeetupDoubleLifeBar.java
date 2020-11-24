package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import org.bukkit.attribute.Attribute;

public class MeetupDoubleLifeBar extends IGamemode implements Listener {
    private UHC instance;

    public MeetupDoubleLifeBar(UHC instance) {
        super("MeetupDoubleLifeBar", "Meetup Double life bar.");
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
    public void onStart(GameTickEvent e) {
        if (e.getSecond() == instance.getGame().getBorderTime()) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                players.setHealth(players.getHealth()+20.0);
            });
        }

    }

}