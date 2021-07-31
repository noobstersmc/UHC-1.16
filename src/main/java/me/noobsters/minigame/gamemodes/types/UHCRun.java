package me.noobsters.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.GameStartedEvent;
import me.noobsters.minigame.gamemodes.GamemodeManager;
import me.noobsters.minigame.gamemodes.IGamemode;
import me.noobsters.minigame.gamemodes.interfaces.ScenarioPack;

public class UHCRun extends IGamemode implements ScenarioPack, Listener {
    private ArrayList<IGamemode> gamemodes = new ArrayList<>();
    private UHC instance;
    private Random random = new Random();

    public UHCRun(UHC instance, GamemodeManager gamemodeManager) {
        super("UHC Run", "An accelerated UHC Experience.", Material.GOLD_BLOCK);
        this.instance = instance;

        /* Add gamemodes to your scenario packs like this */
        gamemodes.add(gamemodeManager.getScenario(Cutclean.class));
        gamemodes.add(gamemodeManager.getScenario(HasteyBoys.class));
        gamemodes.add(gamemodeManager.getScenario(Timber.class));
        //gamemodes.add(gamemodeManager.getScenario(FastLeaves.class));
        gamemodes.add(gamemodeManager.getScenario(LuckyLeaves.class));
        gamemodes.add(gamemodeManager.getScenario(FastSmelting.class));

    }

    @Override
    public ArrayList<IGamemode> getGamemodes() {
        return gamemodes;
    }

    @Override
    public String getDescription() {
        String addedDescription = "";
        for (var scen : gamemodes) {
            if (!scen.isEnabled()) {
                addedDescription = addedDescription + " " + scen.getName() + " disabled";
            }
        }
        return super.getDescription() + addedDescription;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        for (var gm : gamemodes) {
            gm.callEnable();
        }

        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        instance.getGame().setNether(false);
        instance.getGame().setBorderTime(1200);
        instance.getGame().setPvpTime(900);
        instance.getGame().setBorderCenterTime(600);
        instance.getGame().setBorderCenter(100);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bordersize 1000");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game score RUN");

        Bukkit.getWorlds().forEach(it -> {
            it.setGameRule(GameRule.RANDOM_TICK_SPEED, 100);
        });

        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;

        for (var gm : gamemodes) {
            if (gm.isEnabled())
                gm.callDisable();
        }

        instance.getGame().setNether(true);
        instance.getGame().setPvpTime(1200);
        instance.getGame().setBorderTime(3600);
        instance.getGame().setBorderCenterTime(1800);
        instance.getGame().setBorderCenter(200);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bordersize 3000");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game score UHC");

        instance.getListenerManager().unregisterListener(this);

        setEnabled(false);

        Bukkit.getWorlds().forEach(it -> {
            it.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
        });

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        giveRunEffects(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStart(GameStartedEvent e) {
        Bukkit.getOnlinePlayers().forEach(this::giveRunEffects);
        instance.getGame().setBorderSize(1000);
        instance.getGame().setPvpTime(1200);
        instance.getGame().setBorderTime(1200);
        instance.getGame().setBorderCenterTime(600);
        instance.getGame().setBorderCenter(100);
    }

    private void giveRunEffects(Player player) {
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.12);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000 * 20, 100, false, false, false));
        player.addPotionEffect(
                new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 10000 * 20, 100, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 10000 * 20, 100, false, false, false));

    }

    @EventHandler
    public void onOtherCraft(PrepareItemCraftEvent e) {
        var result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        var re = result.getType().toString();
        if (re.contains("WOODEN") && !re.contains("SWORD")) {
            result.setType(Material.matchMaterial(re.replace("WOODEN", "STONE")));
            ItemMeta meta = result.getItemMeta();
            meta.addEnchant(Enchantment.DIG_SPEED, 3, false);
            meta.addEnchant(Enchantment.DURABILITY, 3, false);
            result.setItemMeta(meta);
        } else if (re.equalsIgnoreCase("book")) {
            result.setAmount(3);
        }

    }

    @EventHandler(ignoreCancelled = true) 
    public void extraXp(BlockBreakEvent e) {
        if(instance.getGame().isAntiMining()) return;

        var loc = e.getBlock().getLocation();
        if (e.getBlock().getType().toString().contains("ORE")) {
            loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(3);
        }

        if (random.nextInt(5) == 0) {
            var block = e.getBlock().getType();
            if (block == Material.GRASS || block == Material.TALL_GRASS || block == Material.SEAGRASS) {
                e.getBlock().setType(Material.SUGAR_CANE);
            }
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.GRAVEL && random.nextBoolean()) {
            stack.setType(Material.ARROW);
        } else if (type.toString().contains("WOOL")) {
            stack.setAmount(4);
            stack.setType(Material.STRING);
        } else if (type.toString().contains("RABBIT_HIDE")) {
            stack.setType(Material.LEATHER);
        } else if (type.toString().contains("GRASS") && random.nextInt(5) == 0) {
            stack.setType(Material.SUGAR_CANE);
        }
    }

    @EventHandler
    public void onMobs(EntityDeathEvent e) {
        var type = e.getEntity().getType();
        if (type == EntityType.COW || type == EntityType.SHEEP || type == EntityType.PIG || type == EntityType.HORSE)
            e.getDrops().add(new ItemStack(Material.LEATHER));
        if (type == EntityType.CHICKEN)
            e.getDrops().add(new ItemStack(Material.ARROW, 3));
        if (type == EntityType.BLAZE && random.nextBoolean())
            e.getDrops().add(new ItemStack(Material.GLOWSTONE));
    }

}
