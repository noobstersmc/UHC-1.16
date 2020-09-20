package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class DebugMode extends IGamemode implements Listener {
    private UHC instance;

    public DebugMode(UHC instance) {
        super("Debug", "Debug mode. Often used for testing purposes.");
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerHitByPlayerDebug(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.PLAYER)
            return;
        var player = (Player) e.getEntity();
        var damager = (Player) e.getDamager();
        final var string = String.format("[DEBUG] %s to %s - DMG: %.2f - FMDG: %.2f", damager.getName(),
                player.getName(), e.getDamage(), e.getFinalDamage());
        Bukkit.broadcast(string, "uhc.debug");
        System.out.println(string);

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

}
