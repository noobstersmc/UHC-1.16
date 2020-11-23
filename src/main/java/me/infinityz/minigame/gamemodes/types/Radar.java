package me.infinityz.minigame.gamemodes.types;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.CompassMeta;

import gnu.trove.map.hash.THashMap;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

public class Radar extends IGamemode implements Listener {
    private UHC instance;
    private THashMap<Long, Long> cooldownHashMap = new THashMap<Long, Long>();
    private double radius = 100;

    public Radar(UHC instance) {
        super("Radar", "Compasses are a radar to search players.");
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
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR && e.getPlayer().getInventory().getItemInMainHand().getItemMeta() instanceof CompassMeta) {
            Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                var compass = e.getItem();
                var compassMeta = (CompassMeta) compass.getItemMeta();
                var player = e.getPlayer();
                Long bits = player.getUniqueId().getMostSignificantBits();
                if (cooldownHashMap.getOrDefault(bits, 0L) <= System.currentTimeMillis()) {
                    cooldownHashMap.put(bits, System.currentTimeMillis() + 1000);
                    var optionalEnemy = getNearestEnemy(player);
                    if (optionalEnemy.isPresent()) {
                        var enemy = optionalEnemy.get();
                        compassMeta.setLodestone(enemy.getLocation());
                        var distance = player.getLocation().distanceSquared(enemy.getLocation());
                        player.sendActionBar(ChatColor.RED + "Now tracking " + enemy.getName() + " distance = " + distance + "m");
                    }
                }
            });

        }
    }

    private Optional<Player> getNearestEnemy(Player from) {
        // Variable setting
        final var teamManager = instance.getTeamManger();
        Team playerTeam = null;
        // Check if it is teams and set playerTeam
        if (instance.getTeamManger().isTeams())
            playerTeam = teamManager.getPlayerTeam(from.getUniqueId());
        // Make the result final to loop in the lambda
        final var localTeam = playerTeam;
        //Return optional object.
        return from.getLocation().getNearbyPlayers(radius).parallelStream()
                .filter(p -> p != from || (localTeam != null && !localTeam.isMember(p.getUniqueId()))).findFirst();
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        var recipe = e.getRecipe();
        if(recipe != null && recipe.getResult().getType() == Material.COMPASS){
            var meta = e.getInventory().getResult().getItemMeta();
            meta.setDisplayName(ChatColor.DARK_RED + "RADAR");
            e.getInventory().getResult().setItemMeta(meta);
        }

    }

}