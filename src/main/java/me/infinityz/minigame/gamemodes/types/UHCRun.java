package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.ScoreboardUpdateEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.gamemodes.interfaces.ScenarioPack;
import me.infinityz.minigame.tasks.GameLoop;
import net.md_5.bungee.api.ChatColor;

public class UHCRun extends IGamemode implements ScenarioPack, Listener {
    private ArrayList<IGamemode> gamemodes = new ArrayList<>();
    private UHC instance;
    private Random random = new Random();
    private WorldBorder worldBorder = Bukkit.getWorlds().get(0).getWorldBorder();

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
        instance.getGame().setBorderTime(1200);
        instance.getGame().setBorderCenterTime(600);
        instance.getGame().setBorderCenter(100);

        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        var coloredTitle = ChatColor.of("#FFC400") + "" + ChatColor.BOLD + "UHC RUN";
        
        Game.setScoreboardTitle(coloredTitle);

        instance.getScoreboardManager().getFastboardMap().values().forEach(all -> all.updateTitle(coloredTitle));
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

    
    /*
     * Scoreboard Interceptor starts
     */

    @EventHandler(priority = EventPriority.LOW)
    public void onInterceptUpate(ScoreboardUpdateEvent e) {
        e.setCancelled(true);
    }
    /*
     * UHC RUN MODIFICATIONS
     * 
     * 
     */

    @EventHandler(priority = EventPriority.HIGH)
    public void onModifyScoreboard(ScoreboardUpdateEvent e) {
        e.setCancelled(false);
        var player = e.getScoreboard().getPlayer();

        var uhcPlayer = instance.getPlayerManager().getPlayer(player.getUniqueId());

        e.setLinesArray(ChatColor.of("#20A127") + "Time: " + ChatColor.WHITE + GameLoop.timeConvert(instance.getGame().getGameTime()), 
        "",
        ChatColor.of("#20A127") + "Kills: " + ChatColor.WHITE + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
        "",
        ChatColor.of("#20A127") + "Players: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(),
        ChatColor.of("#20A127") + "Border: " + ChatColor.WHITE + ((int) worldBorder.getSize() / 2), 
        "",
        ChatColor.WHITE + "noobsters.net");

        
    }

    /*
     * Scoreboard Interceptor ends
     */

    @EventHandler
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000 * 20, 100, false, false ,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 10000* 20, 4, false, false ,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 10000* 20, 100, false, false ,false));

    }

    @EventHandler
    public void extraXp(BlockBreakEvent e){
        var loc = e.getBlock().getLocation();
        if(e.getBlock().getType().toString().contains("ORE")){
            loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(3);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.GRAVEL) {
            if(random.nextBoolean()){
                stack.setType(Material.ARROW);
            }
        }
        if(type.toString().contains("WOOL")){
            stack.setAmount(4);
            stack.setType(Material.STRING);
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        var recipe = e.getRecipe();
        if(recipe != null && recipe.getResult().getType() == Material.BOOK){
            e.getInventory().setResult(new ItemBuilder(Material.BOOK).amount(3).build());
        }

    }

    

    // starting min players

    // scoreboard custom lobby

}
