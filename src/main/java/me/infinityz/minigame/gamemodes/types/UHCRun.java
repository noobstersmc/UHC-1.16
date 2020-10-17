package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.gamemodes.interfaces.ScenarioPack;

public class UHCRun extends IGamemode implements ScenarioPack, Listener {
    private ArrayList<IGamemode> gamemodes = new ArrayList<>();
    private UHC instance;
    private Random random = new Random();

    public UHCRun(UHC instance) {
        super("UHC Run", "An accelerated UHC Experience.");
        this.instance = instance;
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            var manager = instance.getGamemodeManager();
            gamemodes.add(manager.getScenario(Cutclean.class));
            gamemodes.add(manager.getScenario(HasteyBoys.class));
            gamemodes.add(manager.getScenario(Timber.class));
            gamemodes.add(manager.getScenario(LuckyLeaves.class));

        }, 5L);
        // Fast leaves decay

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
        gamemodes.forEach(scenarios -> {
            scenarios.enableScenario();
        });

        instance.getGame().setNether(false);
        instance.getGame().setBorderSize(1000);
        instance.getGame().setPvpTime(1200);
        instance.getGame().setBorderTime(1500);
        instance.getGame().setBorderCenterTime(600);
        instance.getGame().setBorderCenter(100);

        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;

        gamemodes.forEach(scenarios -> {
            scenarios.disableScenario();
        });

        instance.getListenerManager().unregisterListener(this);

        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        giveRunEffects(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStart(GameStartedEvent e) {
        Bukkit.getOnlinePlayers().forEach(this::giveRunEffects);
        instance.getGame().setBorderSize(1000);
        instance.getGame().setPvpTime(1200);
        instance.getGame().setBorderTime(1500);
        instance.getGame().setBorderCenterTime(600);
        instance.getGame().setBorderCenter(100);
    }

    private void giveRunEffects(Player player) {
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);
        player.getAttribute(Attribute.GENERIC_LUCK).setBaseValue(20.0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000 * 20, 4, false, false ,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 10000* 20, 4, false, false ,false));

    }

    @EventHandler
    public void extraXp(BlockBreakEvent e){
        var loc = e.getBlock().getLocation();
        if(e.getBlock().getType().toString().contains("ORE")){
            loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(4);
        }
    }

    @EventHandler
    public void arrowGravel(BlockBreakEvent e){
        var loc = e.getBlock().getLocation();
        var rand = random.nextInt(4);
        if(e.getBlock().getType().equals("GRAVEL")){
            if (rand + 1 <= 2) {
                dropCenter(new ItemStack(Material.IRON_INGOT, (rand)), loc);

            }
            loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(4);
        }
    }

    Location dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.1, 0.0));
        return centeredLocation;
    }

    // starting min players

    // scoreboard custom lobby

    // generacion custom mas ores en todas las capas y estructuras del nether en
    // over

}
