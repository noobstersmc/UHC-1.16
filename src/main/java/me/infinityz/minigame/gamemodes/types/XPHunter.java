package me.infinityz.minigame.gamemodes.types;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

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

        Bukkit.getOnlinePlayers().forEach(players -> {

                UHC.newChain().delay(20).sync(() -> {
                    players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
                }).sync(TaskChain::abort).execute();
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoinLate(PlayerJoinedLateEvent e){
        var player = e.getPlayer();
        UHC.newChain().delay(20).sync(() -> {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2.0);
        }).sync(TaskChain::abort).execute();
    }


    @EventHandler
    public void onXpAdd(PlayerExpChangeEvent e){
        Bukkit.broadcastMessage("called");
        int level = e.getPlayer().getLevel()*2;
        var playerHP = e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        if(level <= 2 || playerHP >= 40) return;
        e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(level);

    }

    

    @EventHandler
    public void onXpRemove(EnchantItemEvent e){
        int level = e.getEnchanter().getLevel()*2;
        var playerHP = e.getEnchanter().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        if(playerHP <= 2) return;
        e.getEnchanter().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(level);
    }

    @EventHandler
    public void onAnvilRemove(AnvilDamagedEvent e){
        var players = e.getViewers();
        players.forEach(viewers ->{
            Player p = (Player) viewers;
            int level = p.getLevel()*2;
            var playerHP = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            if(playerHP <= 2) return;
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(level);
        });
    }   
      

}