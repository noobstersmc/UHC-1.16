package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class DoubleGold extends IGamemode implements Listener{
    private UHC instance;

    public DoubleGold(UHC instance) {
        super("DoubleGold", "Gold drop twice.", Material.GOLD_INGOT);
        setIconCount(2);
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if(isEnabled() || instance.getGamemodeManager().isScenarioEnable(TripleOres.class)
        || instance.getGamemodeManager().isScenarioEnable(DoubleOres.class)){
            return false;
        }
        instance.getListenerManager().registerListener(this);
        instance.getGamemodeManager().setExtraGold(1);
        setEnabled(true);
        return true;
    }
    @Override
    public boolean disableScenario() {
        if(!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        instance.getGamemodeManager().setExtraGold(0);
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.GOLD_ORE)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "DoubleGold Scenario do not allow this.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakCut(final BlockBreakEvent e) {

        final var block = e.getBlock();

    if (!instance.getGamemodeManager().isScenarioEnable(Cutclean.class) && 
            block.getType().equals(Material.GOLD_ORE)) {
        e.setDropItems(false);
        dropCenter(new ItemStack(Material.GOLD_ORE, 2), block.getLocation());

    }
    }
    
    Location dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.1, 0.0));
        return centeredLocation;
    }

}