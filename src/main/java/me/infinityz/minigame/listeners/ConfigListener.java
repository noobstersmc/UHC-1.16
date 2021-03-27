package me.infinityz.minigame.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityMountEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import net.md_5.bungee.api.ChatColor;

public class ConfigListener implements Listener {

    private UHC instance;
    private Random random = new Random();

    public ConfigListener(UHC instance) {
        this.instance = instance;
    }

    public int randomN(Integer i) {
        var rand = random.nextInt(i);
        if (rand != 0)
            return rand;
        return i;
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        if (instance.getGame().isTears())
            return;
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.GHAST_TEAR) {
            stack.setType(Material.GOLD_INGOT);
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        if (!instance.getGame().isNether() && e.getReason() == CreateReason.FIRE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void brewing(BrewingStandFuelEvent e) {
        if (!instance.getGame().isPotions()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void trade(InventoryOpenEvent e) {
        var inv = e.getInventory();
        if (inv.getType() == InventoryType.MERCHANT && !instance.getGame().isTrades()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Trading is disabled.");
        }
    }

    @EventHandler
    public void horses(EntityMountEvent e) {
        var entity = e.getMount();
        var player = e.getEntity();
        if (!instance.getGame().isHorses() && entity instanceof Horse && player instanceof Player) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Horses are disabled.");
        }
    }

    @EventHandler
    public void beds(BlockPlaceEvent e) {
        if (!instance.getGame().isBeds() && e.getBlock().getType().toString().contains("BED")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Beds are disabled.");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (instance.getGame().isItemsBurn())
            return;
        if (e.getEntityType() == EntityType.DROPPED_ITEM && (e.getCause() == EntityDamageEvent.DamageCause.FIRE
                || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || e.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoinInNether(PlayerJoinEvent e) {
        if (!instance.getGame().isNether()) {// If nether is disabled
            var player = e.getPlayer();
            // If player's world is nether, scatter them in the overworld.
            if (player.getWorld().getEnvironment() == Environment.NETHER) {
                var worldToTeleport = Bukkit.getWorld("world");
                var radius = (int) worldToTeleport.getWorldBorder().getSize() / 2;
                // Teleport Async to save resources.
                player.teleportAsync(
                        ChunksManager.centerLocation(ChunksManager.findScatterLocation(worldToTeleport, radius)));
            }
        }
    }

    /**
     * APPLE RATE
     */
    @EventHandler
    public void onLeaf(LeavesDecayEvent e) {
        if (instance.getGame().getAppleRate() == 0.5)
            return;
        if (Math.random() <= (instance.getGame().getAppleRate() / 100)) {
            e.getBlock().setType(Material.AIR);
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
        }

    }

    /**
     * FLINT RATE
     */
    @EventHandler
    public void onFlint(BlockBreakEvent e) {
        var block = e.getBlock();
        if (block.getType() == Material.GRAVEL && Math.random() <= (instance.getGame().getFlintRate() / 100)) {
            e.setDropItems(false);
            Bukkit.getWorld(block.getWorld().getName().toString()).dropItemNaturally(block.getLocation(),
                    new ItemStack(Material.FLINT));
        }

    }

    /**
     * Strength nerf
     */

    @EventHandler
    public void strength(BrewEvent e) {
        if (!instance.getGame().isStrength() && e.getContents().getIngredient().getType() == Material.BLAZE_POWDER) {
            e.setCancelled(true);
            return;
        }
        /*
        if (instance.getGame().isStrengthNerf()) {
            var storage = e.getContents().getStorageContents();
            ItemStack[] potions = {};
            for (int i = 0; i < storage.length; i++) {
                var potion = storage[i];
                if(potion.getItemMeta() instanceof PotionMeta){
                    PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

                final var type = potionMeta.getBasePotionData().getType();

                if (type == PotionType.STRENGTH) {
                    final var upgraded = potionMeta.getBasePotionData().isUpgraded();
                    final var extended = potionMeta.getBasePotionData().isExtended();

                    final String[] lore = {};

                    switch (potion.getType()) {

                        case LINGERING_POTION:{
                            if(upgraded){
                                lore[0] = ChatColor.BLUE + "Strength II (0:22)";
                                lore[1] = "";
                                lore[2] = ChatColor.DARK_PURPLE + "When Applied:";
                                lore[3] = ChatColor.BLUE + "+3 Attack Damage";
                            }else if(extended){
                                lore[0] = ChatColor.BLUE + "Strength (2:00)";
                                lore[1] = "";
                                lore[2] = ChatColor.DARK_PURPLE + "When Applied:";
                                lore[3] = ChatColor.BLUE + "+1.5 Attack Damage";
                            }else{
                                lore[0] = ChatColor.BLUE + "Strength (0:45)";
                                lore[1] = "";
                                lore[2] = ChatColor.DARK_PURPLE + "When Applied:";
                                lore[3] = ChatColor.BLUE + "+1.5 Attack Damage";
                            }
                        }break;

                        default:{
                            if(upgraded){
                                lore[0] = ChatColor.BLUE + "Strength II (1:30)";
                                lore[1] = "";
                                lore[2] = ChatColor.DARK_PURPLE + "When Applied:";
                                lore[3] = ChatColor.BLUE + "+3 Attack Damage";
                            }else if(extended){
                                lore[0] = ChatColor.BLUE + "Strength (8:00)";
                                lore[1] = "";
                                lore[2] = ChatColor.DARK_PURPLE + "When Applied:";
                                lore[3] = ChatColor.BLUE + "+1.5 Attack Damage";
                            }else{
                                lore[0] = ChatColor.BLUE + "Strength (3:00)";
                                lore[1] = "";
                                lore[2] = ChatColor.DARK_PURPLE + "When Applied:";
                                lore[3] = ChatColor.BLUE + "+1.5 Attack Damage";
                            }
                        }break;

                        }

                    var item = new ItemBuilder(potion.getType()).addLore(lore)
                            .meta(PotionMeta.class,
                                    meta -> meta.setBasePotionData(new PotionData(type, extended, upgraded)))
                            .flags(ItemFlag.HIDE_POTION_EFFECTS).build();

                    potions[i] = item;
                }
                }

            }
            e.getContents().setStorageContents(potions);
        }*/

    }

    @EventHandler
    public void strengthFix(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.PLAYER || !instance.getGame().isStrengthNerf())
            return;
        var damager = (Player) e.getDamager();
        var strength = damager.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
        if (strength != null) {
            var strengthAmplifier = 1 + strength.getAmplifier();
            var differential = strengthAmplifier * 1.5;
            e.setDamage(e.getDamage() - differential);
        }

    }

    /*
     * End portal cancel creation
     */

    @EventHandler
    public void onInteractWithPortal(PlayerInteractEvent e) {
        if (!instance.getGame().isEnd() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null
                && e.getItem().getType() == Material.ENDER_EYE)
            e.setCancelled(true);

    }

    /** INCREASED TRIDENT DROP START */

    @EventHandler
    public void onDrownedDeath(EntityDeathEvent e) {
        if (!instance.getGame().isTrident() || e.getEntity().getType() != EntityType.DROWNED)
            return;
        var drowned = (Drowned) e.getEntity();
        var equipment = drowned.getEquipment();
        var mainHand = drowned.getEquipment().getItemInMainHand().clone();
        var offHand = drowned.getEquipment().getItemInOffHand().clone();
        equipment.clear();

        if (mainHand.getType() != Material.AIR) {
            if (mainHand.getType() == Material.TRIDENT) {
                Damageable dmg = (Damageable) mainHand.getItemMeta();
                dmg.setDamage(new Random().nextInt(125));
                mainHand.setItemMeta((ItemMeta) dmg);

            }
            drowned.getLocation().getWorld().dropItemNaturally(drowned.getLocation(), mainHand);

        }
        if (offHand.getType() != Material.AIR) {
            if (offHand.getType() == Material.TRIDENT) {
                Damageable dmg = (Damageable) offHand.getItemMeta();
                dmg.setDamage(new Random().nextInt(125));
                offHand.setItemMeta((ItemMeta) dmg);
            }
            drowned.getLocation().getWorld().dropItemNaturally(drowned.getLocation(), offHand);
        }
    }

    /*
     * BED NERF
     */

    @EventHandler
    public void nerfBedExplosion(PlayerBedEnterEvent e) {
        if (instance.getGame().isBedsNerf() && e.getBed().getWorld().getEnvironment() == Environment.NETHER) {
            e.setCancelled(true);
            e.setUseBed(Result.DENY);
            e.getBed().setType(Material.AIR);
            e.getBed().getLocation().createExplosion(2.0f, true, true);
        }

    }

}
