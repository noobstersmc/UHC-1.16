package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;

public class CutcleanListener implements Listener {
    private @Getter @Setter int ironXp = 1;
    private @Getter @Setter int goldXp = 2;
    private @Getter @Setter int sandXp = 1;
    private @Getter @Setter int netheriteXp = 5;
    private Random random = new Random();

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {

        final var block = e.getBlock();
        final var player = e.getPlayer();
        final var itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR || itemInHand.containsEnchantment(Enchantment.SILK_TOUCH))
            return;

        Bukkit.broadcastMessage(e.getBlock().getType().toString());

        switch (block.getType()) {
            case IRON_ORE: {
                if (!block.getDrops(itemInHand, player).isEmpty()) {

                    final int fortune = fortuneMultiplier(itemInHand);
                    e.setDropItems(false);

                    e.setExpToDrop(ironXp * fortune);

                    dropCenter(new ItemStack(Material.IRON_INGOT, fortune), block.getLocation());

                }
                break;
            }
            case GOLD_ORE: {
                if (!block.getDrops(itemInHand, player).isEmpty()) {
                    final int fortune = fortuneMultiplier(itemInHand);

                    e.setDropItems(false);

                    e.setExpToDrop(goldXp * fortune);

                    dropCenter(new ItemStack(Material.GOLD_INGOT, fortune), block.getLocation());
                }
                break;
            }
            case SAND:
                if (!player.isSneaking())
                    break;

                if (!block.getDrops(itemInHand, player).isEmpty()) {
                    final int fortune = fortuneMultiplier(itemInHand);
                    e.setDropItems(false);

                    //10% chance of getting xp
                    if (random.nextInt(5) + 1 <= 1 * fortune) {
                        e.setExpToDrop(sandXp);

                    }

                    dropCenter(new ItemStack(Material.GLASS, fortune), block.getLocation());
                }
                break;
            case ANCIENT_DEBRIS:
                break;
            default:
                break;
        }

    }

    @EventHandler
    public void onDispense(BlockDropItemEvent e){
        e.getItems().forEach(all->{
            Bukkit.broadcastMessage(all.getType() + " drop");
        });
        e.getItems().clear();
        
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        switch (e.getEntityType()) {
            case COW:
            case CHICKEN:
            case PIG:
            case SHEEP:
            case SALMON:
            case COD:
            case RABBIT:
                e.getDrops().forEach(it -> {
                    if (it.getType().isEdible()) {
                        final Material cookedType = Material
                                .getMaterial("COOKED_" + it.getType().toString().toUpperCase());
                        if (cookedType != null)
                            it.setType(cookedType);
                    }
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

    private int fortuneMultiplier(final ItemStack itemstack) {
        if (itemstack.getType() == Material.AIR || !itemstack.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            return 1;
        final int fortuneLevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        int bonus = random.nextInt(fortuneLevel);
        if (bonus == 0)
            bonus = 1;
        return bonus;
    }

    boolean isTool(Material material) {
        return material.toString().contains("PICKAXE") || material.toString().contains("AXE")
                || material.toString().contains("SPADE") || material.toString().contains("HOE");
    }

}