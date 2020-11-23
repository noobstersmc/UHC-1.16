package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class AdvancementHunter extends IGamemode implements Listener {
    private UHC instance;

    public AdvancementHunter(UHC instance) {
        super("Advancement Hunter", "You get 1 heart for each advancement.");
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
    public void onAdvancement(PlayerAdvancementDoneEvent e) {
        var adv = e.getAdvancement().getKey().getKey();
        if (adv.startsWith("husbandry") || adv.startsWith("story") || adv.startsWith("nether") || adv.startsWith("adventure")
                || adv.startsWith("end")) {
            var playerHealth = e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(playerHealth + 2);
        }
    }

    @EventHandler
    public void onStart(GameStartedEvent e) {
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.getOnlinePlayers()
                        .forEach(players -> players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0));

            });
    }

    @EventHandler
    public void onJoinLate(PlayerJoinedLateEvent e){
        e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
    }

}