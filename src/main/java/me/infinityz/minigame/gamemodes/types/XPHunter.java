package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import org.bukkit.attribute.Attribute;

public class XPHunter extends IGamemode implements Listener {
    private UHC instance;

    public XPHunter(UHC instance) {
        super("XPHunter", "Get experience to get more red hearts.");
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
        Bukkit.getScheduler().runTask(instance, () -> {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
                players.setLevel(2);
            });

        });
    }

    @EventHandler
    public void onXpChange(PlayerExpChangeEvent e){
        e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(e.getAmount());

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinLate(PlayerJoinedLateEvent e){
        var player = e.getPlayer();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
        player.setLevel(2);
    }

}