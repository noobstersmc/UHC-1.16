package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class Soup extends IGamemode implements Listener {
    private UHC instance;
    Random random = new Random();

    public Soup(UHC instance) {
        super("Soup", "Soup is the new golden apple.", Material.MUSHROOM_STEW);
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
    public void eatSoup(PlayerInteractEvent e) {
        var player = e.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if(item != null && e.getAction()!= null && e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(item.getType() == Material.MUSHROOM_STEW){
                player.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0, true, false));
            }else if(item.getType() == Material.RABBIT_STEW){
                player.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1, true, false));
            }
        }

    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e){
        var block = e.getBlock().getType().toString();
        if(!block.contains("STRIPPED") && block.contains("WOOD") || block.contains("LOG")){
            var drop = new ItemStack(random.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM, random.nextInt(3));
            e.getBlock().getDrops().add(drop);
        }
    }
}