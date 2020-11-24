package me.infinityz.minigame.gamemodes.types;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import co.aikar.taskchain.TaskChain;
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
    /*
    @EventHandler
    public void onShoot(PlayerInteractEvent e){
        var item = e.getPlayer().getInventory().getItemInMainHand();
        if(item.getType() == Material.CROSSBOW){
            if(e.getAction() == Action.RIGHT_CLICK_AIR){
                var meta = (CrossbowMeta) item.getItemMeta();
                if(meta.getChargedProjectiles().isEmpty()) return;
                var proyectiles = List.of(new ItemStack(Material.ARROW));
                meta.setChargedProjectiles(proyectiles);

            }
        }

    }*/
    
    @EventHandler
    public void onShoorV2(EntityShootBowEvent e ){
        var item = e.getBow();
        if(item.getType() == Material.CROSSBOW){
            var player = (Player) e.getEntity();
            var ammo = new ItemStack(Material.ARROW);
            if(player.getInventory().containsAtLeast(ammo, 1)){
                UHC.newChain().delay(2).sync(() -> {
                    var meta = (CrossbowMeta) item.getItemMeta();
                    var proyectiles = List.of(ammo);
                    meta.setChargedProjectiles(proyectiles);
                    item.setItemMeta(meta);
                }).sync(TaskChain::abort).execute();
            }

        }
    }
    

}