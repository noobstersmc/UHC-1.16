package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class TripleOres extends IGamemode implements Listener{
    private UHC instance;
    private @Getter @Setter int diamondXp = 7;
    private @Getter @Setter int coalXp = 2;
    private @Getter @Setter int emeraldXp = 5;
    private @Getter @Setter int quartzXp = 5;
    private Random random = new Random();

    public TripleOres(UHC instance) {
        super("TripleOres", "All ores drop three times.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if(isEnabled() || instance.getGamemodeManager().isScenarioEnable(DoubleOres.class) 
        || instance.getGamemodeManager().isScenarioEnable(DoubleGold.class)){
            return false;
        }
        instance.getListenerManager().registerListener(this);
        instance.getGamemodeManager().setExtraOreAmount(2);
        setEnabled(true);
        return true;
    }
    @Override
    public boolean disableScenario() {
        if(!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        instance.getGamemodeManager().setExtraOreAmount(0);
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.IRON_ORE) 
        || e.getBlock().getType().equals(Material.ANCIENT_DEBRIS)
        || e.getBlock().getType().equals(Material.GOLD_ORE)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "TripleOres Scenario do not allow this.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakCut(final BlockBreakEvent e) {

        final var block = e.getBlock();

    if (!instance.getGamemodeManager().isScenarioEnable(Cutclean.class)) {
        
        switch (block.getType()) {
            case IRON_ORE: {
                e.setDropItems(false);
                dropCenter(new ItemStack(Material.IRON_ORE, 3), block.getLocation());
                break;
            }
            case GOLD_ORE: {
                e.setDropItems(false);
                dropCenter(new ItemStack(Material.GOLD_ORE, 3), block.getLocation());
                break;
            }
            default:
            break;
        }

    }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {
        final var block = e.getBlock();
        final var player = e.getPlayer();
        final var itemInHand = player.getInventory().getItemInMainHand();
        switch (block.getType()) {
            case ANCIENT_DEBRIS: {
                e.setDropItems(false);
                dropCenter(new ItemStack(Material.ANCIENT_DEBRIS, 3), block.getLocation());
                break;
            }
            case DIAMOND_ORE: {
                final int fortune = fortuneMultiplier(itemInHand);
                e.setDropItems(false);
                dropCenter(new ItemStack(Material.DIAMOND, (2+fortune)), block.getLocation());
                e.setExpToDrop(diamondXp);
                
                break;
            }
            case EMERALD_ORE: {
                final int fortune = fortuneMultiplier(itemInHand);
                e.setDropItems(false);
                dropCenter(new ItemStack(Material.EMERALD, (2+fortune)), block.getLocation());
                e.setExpToDrop(emeraldXp);
                break;
            }
            case NETHER_QUARTZ_ORE: {
                final int fortune = fortuneMultiplier(itemInHand);
                e.setDropItems(false);
                dropCenter(new ItemStack(Material.QUARTZ, (2+fortune)), block.getLocation());
                e.setExpToDrop(quartzXp);
                break;
            }
            case COAL_ORE: {
                final int fortune = fortuneMultiplier(itemInHand);
                e.setDropItems(false);
                dropCenter(new ItemStack(Material.COAL, (2+fortune)), block.getLocation());
                if (random.nextDouble() <= 0.30) {
                    e.setExpToDrop(coalXp);

                }
                break;
            }
            default:
            break;

        }
    }
    
    void summonXPOrb(Location loc, int amount) {
        loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(amount);

    }

    private int fortuneMultiplier(final ItemStack itemstack) {
        if (itemstack.getType() == Material.AIR || !itemstack.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            return 1;
        final int fortuneLevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        int bonus = random.nextInt(fortuneLevel);
        if (bonus == 0)
            bonus = 1;
        return bonus;
    }
    
    Location dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.1, 0.0));
        return centeredLocation;
    }

}