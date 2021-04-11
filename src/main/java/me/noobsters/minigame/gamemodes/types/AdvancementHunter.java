package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.GameStartedEvent;
import me.noobsters.minigame.events.PlayerJoinedLateEvent;
import me.noobsters.minigame.gamemodes.IGamemode;

public class AdvancementHunter extends IGamemode implements Listener {
    private UHC instance;

    public AdvancementHunter(UHC instance) {
        super("Advancement Hunter", "Players start the game with 1 red heart,\nThey will get 1 additional heart\nfor each completed advancement.", Material.KNOWLEDGE_BOOK);
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
            e.getPlayer().setHealth(e.getPlayer().getHealth()+2);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStart(GameStartedEvent e) {

        Bukkit.getOnlinePlayers().forEach(players -> {

                Bukkit.getScheduler().runTaskLater(instance, ()->{
                    players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
                }, 20);
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinLate(PlayerJoinedLateEvent e){
        var player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(instance, ()->{
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
        }, 20);
    }

}