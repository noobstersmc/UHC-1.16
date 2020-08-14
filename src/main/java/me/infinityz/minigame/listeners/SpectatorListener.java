package me.infinityz.minigame.listeners;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.mrmicky.fastinv.FastInv;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.game.UpdatableInventory;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class SpectatorListener implements Listener {
    private @NonNull UHC instance;

    @EventHandler
    public void invSpecEvent(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() == null || e.getRightClicked().getType() != EntityType.PLAYER)
            return;
        if (!e.getPlayer().hasPermission("staff.perm") || e.getPlayer().getGameMode() != GameMode.SPECTATOR)
            return;
        var player = e.getPlayer();
        var clicked = (Player) e.getRightClicked();
        // TODO: Add a specInv manager to share inventories and not open one viewer.

        var fastInv = new UpdatableInventory(5 * 9, clicked.getName() + "'s inventory'");
        fastInv.addUpdateTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (isCancelled()) {
                    cancel();
                    return;
                }
                updateInventory(fastInv, clicked);
            }

        }, instance, 0, 20, true);
        fastInv.open(player);
    }

    public void onJoinHide(PlayerJoinEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            Bukkit.getOnlinePlayers().stream().filter(all -> all.getGameMode() != GameMode.SPECTATOR)
                    .forEach(all -> all.hidePlayer(instance, e.getPlayer()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpecChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && !e.getPlayer().hasPermission("staff.perm")) {
            e.setCancelled(true);

            e.getRecipients().stream()
                    .filter(it -> it.getGameMode() == GameMode.SPECTATOR || it.hasPermission("staff.perm"))
                    .forEach(specs -> {
                        specs.sendMessage(ChatColor.GRAY + "[SPEC] " + e.getFormat()
                                .replace("%1$s", e.getPlayer().getName()).replace("%2$s", e.getMessage()));
                    });
        }

    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SPECTATOR) {
            Bukkit.getOnlinePlayers().stream().filter(players -> players.getGameMode() == GameMode.SURVIVAL)
                    .forEach(all -> all.hidePlayer(instance, e.getPlayer()));
        } else {
            Bukkit.getOnlinePlayers().stream().filter(player -> !player.canSee(e.getPlayer()))
                    .forEach(all -> all.showPlayer(instance, e.getPlayer()));

        }

    }

    private void updateInventory(FastInv fastInv, Player target) {
        var count = 0;
        for (var itemStack : target.getInventory()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                fastInv.setItem(count, new ItemStack(Material.AIR));
            } else {
                fastInv.setItem(count, itemStack);
            }
            count++;
        }
        // Obtain a list of all the active potion effects as strings
        var effects = target
                .getActivePotionEffects().stream().map(it -> ChatColor.GRAY + it.getType().getName() + " "
                        + (1 + it.getAmplifier()) + ": " + ChatColor.WHITE + (it.getDuration() / 20) + "s")
                .collect(Collectors.toList());
        // Create a new Item Stack
        var potionEffectsItem = new ItemStack(Material.GLASS_BOTTLE);
        // Obtain the meta
        var potionEffectsItemMeta = potionEffectsItem.getItemMeta();
        // Change the meta
        potionEffectsItemMeta.setDisplayName(ChatColor.GOLD + "Active Potion Effects:");
        potionEffectsItemMeta.setLore(effects);
        potionEffectsItem.setItemMeta(potionEffectsItemMeta);
        // Add the item to the inventory 41 is the one next to the offhand item.
        fastInv.setItem(41, potionEffectsItem);
        // Repeat for Health
        var healthItem = new ItemStack(Material.RED_BANNER);
        var healthItemMeta = healthItem.getItemMeta();
        healthItemMeta.setDisplayName(ChatColor.GOLD + "Health:");
        healthItemMeta.setLore(List.of(ChatColor.WHITE + "Hearts: " + target.getHealth(),
                ChatColor.WHITE + "Absorption: " + target.getAbsorptionAmount()));
        healthItem.setItemMeta(healthItemMeta);
        fastInv.setItem(42, healthItem);
        // Repeat for EXP values
        var experienceItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        var experienceItemMeta = experienceItem.getItemMeta();
        experienceItemMeta.setDisplayName(ChatColor.GOLD + "Experience:");
        experienceItemMeta.setLore(List.of(ChatColor.WHITE + "Levels: " + target.getLevel(),
                ChatColor.WHITE + "Percent to next level: " + String.format("%.2f", target.getExp() * 100)));
        experienceItem.setItemMeta(experienceItemMeta);
        fastInv.setItem(43, experienceItem);

    }

}