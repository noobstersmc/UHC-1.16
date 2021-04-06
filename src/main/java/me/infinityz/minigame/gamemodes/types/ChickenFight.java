package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class ChickenFight extends IGamemode implements Listener {
    private UHC instance;
    Random random = new Random();

    public ChickenFight(UHC instance) {
        super("ChickenFight", "Players can carry their teammates on their back.", Material.EGG);
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled() || !instance.getTeamManger().isTeams())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled() || !instance.getTeamManger().isTeams())
            return false;
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onMountMate(PlayerInteractAtEntityEvent e){
        var entity = e.getRightClicked();
        var rider = e.getPlayer();
        if(entity instanceof Player){
            var player = (Player) entity;
            var riderTeam = instance.getTeamManger().getPlayerTeam(rider.getUniqueId());
            if(riderTeam != null && riderTeam.isMember(player.getUniqueId())){
                player.addPassenger(rider);
            }
        }

    }


}