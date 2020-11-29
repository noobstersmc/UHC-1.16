package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class UHCLatamT2 extends IGamemode implements Listener {
    private UHC instance;

    public UHCLatamT2(UHC instance) {
        super("UHC Latam T2", "Kernel es muy tierno");
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
        if (e.getSecond() == 0) {
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "scoreboard objectives modify health_name rendertype hearts");
            });
        }

    }
    
    @EventHandler
    public void onBorderDamage(EntityDamageEvent e){
        if(e.getCause() == DamageCause.CUSTOM){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.POTATO) {
            stack.setType(Material.BAKED_POTATO);
        } else if (type == Material.ANCIENT_DEBRIS) {
            stack.setType(Material.NETHERITE_SCRAP);
        } else if (type == Material.KELP) {
            stack.setType(Material.DRIED_KELP);
        }
    }


                
}