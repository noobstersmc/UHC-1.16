package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class XPHunter extends IGamemode implements Listener {
    private UHC instance;

    public XPHunter(UHC instance) {
        super("Experience Hunter", "Get experience to get more red hearts.", Material.EXPERIENCE_BOTTLE);
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

            Bukkit.getScheduler().runTaskLater(instance, () -> {
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
            }, 20);
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinLate(PlayerJoinedLateEvent e) {
        var player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
        }, 20);
    }

    @EventHandler
    public void xpChange(PlayerLevelChangeEvent e) {
        final var player = e.getPlayer();

        var diff = Math.abs(e.getOldLevel() - e.getNewLevel());
        var playerMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        if (e.getNewLevel() > e.getOldLevel()) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerMax + (diff * 2));
            player.setHealth(player.getHealth() + (diff * 2));

        } else {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerMax - (diff * 2));
            player.damage(diff * 2);
        }

    }

}