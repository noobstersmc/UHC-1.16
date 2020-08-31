package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;

public class CutcleanListener implements Listener {
    private @Getter @Setter int ironXp = 2;
    private @Getter @Setter int goldXp = 5;

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        var block = e.getBlock();
        var player = e.getPlayer();

        switch (block.getType()) {
            case IRON_ORE:
                unbreaking_damage(player);
                e.getBlock().setType(Material.AIR);
                summonXPOrb(dropCenter(new ItemStack(Material.IRON_INGOT, 1 + fortune_bonus(player)),
                        e.getBlock().getLocation()), ironXp);
                break;
            case GOLD_ORE:
                unbreaking_damage(player);
                e.getBlock().setType(Material.AIR);
                summonXPOrb(dropCenter(new ItemStack(Material.GOLD_INGOT, 1 + fortune_bonus(player)),
                        e.getBlock().getLocation()), goldXp);
                break;

            default:
                break;
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        switch (e.getEntityType()) {
            case COW: 
                e.getDrops().forEach(it -> {
                    if (it.getType() != Material.BEEF)
                        return;
                    it.setType(Material.COOKED_BEEF);
                });
                break;
            
            case CHICKEN: 
                e.getDrops().forEach(it -> {
                    if (it.getType() != Material.CHICKEN)
                        return;
                    it.setType(Material.COOKED_CHICKEN);
                });
                break;
            
            case PIG: 
                e.getDrops().forEach(it -> {
                    if (it.getType() != Material.PORKCHOP)
                        return;
                    it.setType(Material.COOKED_PORKCHOP);
                });
                break;
            
            case SHEEP: 
                e.getDrops().forEach(it -> {
                    if (it.getType() != Material.MUTTON)
                        return;
                    it.setType(Material.COOKED_MUTTON);
                });
                break;
            
            case SALMON: 
                e.getDrops().forEach(it -> {
                    if (it.getType() != Material.SALMON)
                        return;
                    it.setType(Material.COOKED_SALMON);
                });
                break;
            
            case COD: 
                e.getDrops().forEach(it -> {
                    if (it.getType() != Material.COD)
                        return;
                    it.setType(Material.COOKED_COD);
                });
                break;
            
            default: 
                break;
            
        }

    }

    void summonXPOrb(Location loc, int amount) {
        loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(amount);

    }

    // Method that ensures ores don't fly like in many other servers
    Location dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.1, 0.0));
        return centeredLocation;
    }

    // Quick int method to obtain what the fortune spell should bonus the player
    // when they mine.
    int fortune_bonus(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR || !hand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            return 0;
        int fortuneLevel = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        int bonus = (int) (Math.random() * (fortuneLevel + 2)) - 1;
        if (bonus < 0) {
            bonus = 0;
        }
        return bonus;
    }

    void unbreaking_damage(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR || !isTool(hand.getType()))
            return;
        if (!hand.containsEnchantment(Enchantment.DURABILITY)) {
            hand.setDurability((short) (hand.getDurability() + 1));
            player.updateInventory();
            return;
        }
        int unbreaking_level = hand.getEnchantmentLevel(Enchantment.DURABILITY);
        double chance = ((100 / (unbreaking_level + 1)) / 100.0D);
        int damage = Math.random() <= chance ? 1 : 0;
        hand.setDurability((short) (hand.getDurability() + damage));
        player.updateInventory();
    }

    boolean isTool(Material material) {
        return material.toString().contains("PICKAXE") || material.toString().contains("AXE")
                || material.toString().contains("SPADE") || material.toString().contains("HOE");
    }

}