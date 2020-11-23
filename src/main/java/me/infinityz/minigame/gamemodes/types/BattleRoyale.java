package me.infinityz.minigame.gamemodes.types;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class BattleRoyale extends IGamemode implements Listener {
    private UHC instance;

    public BattleRoyale(UHC instance) {
        super("Battle Royale", "Battle Royale.");
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
    public void onShoot(PlayerInteractEvent e){
        e.getPlayer().getInventory().getItemInMainHand();
    }

}