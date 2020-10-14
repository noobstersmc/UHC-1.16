package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class Switcheroo extends IGamemode implements Listener {
    private UHC instance;

    public Switcheroo(UHC instance) {
        super("Switcheroo", "When you shoot someone else you swap positions.");
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
    public void onShoot(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            if ((e.getDamager() instanceof Projectile)){
                final var projectile = (Projectile) e.getDamager();
                if(projectile.getShooter() instanceof Player){
                    final var player1 = (Player) projectile.getShooter();
                    final var player2 = (Player) e.getEntity();
                    final var teleport1 = player1.getLocation();
                    final var teleport2 = player2.getLocation();
                    player1.teleport(teleport2);
                    player2.teleport(teleport1);
                    player1.playSound(teleport2, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                    player2.playSound(teleport1, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                }
            }
        }
    }





}