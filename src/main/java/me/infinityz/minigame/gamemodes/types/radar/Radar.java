package me.infinityz.minigame.gamemodes.types.radar;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.CompassMeta;

import gnu.trove.map.hash.THashMap;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.teams.objects.Team;

public class Radar extends IGamemode implements Listener {
    private UHC instance;
    private RadarRecipe recipe;
    private THashMap<Long, Long> cooldownHashMap = new THashMap<Long, Long>();
    private double radius = 100;

    public Radar(UHC instance) {
        super("Radar", "A Radar to search players can be crafted.");
        this.instance = instance;
        this.recipe = new RadarRecipe(new NamespacedKey(instance, "radar"), null);
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        Bukkit.addRecipe(recipe.getRecipe());
        Bukkit.getOnlinePlayers().forEach(all -> all.discoverRecipe(this.recipe.getNamespacedKey()));
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        Bukkit.removeRecipe(recipe.getNamespacedKey());
        Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(this.recipe.getNamespacedKey()));
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.PHYSICAL && e.getItem().getItemMeta() instanceof CompassMeta) {
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
                        player.sendActionBar("Now tracking " + enemy.getName() + " distance = " + distance + "m");
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
    public void onAnvilRename(InventoryClickEvent e) {
        var clickedInventory = e.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getType() == InventoryType.ANVIL) {
            var itemSlot0 = e.getInventory().getItem(0);
            if (itemSlot0.getI18NDisplayName().contains("RADAR")){
                e.setCancelled(true);
            }
        }
    }

}