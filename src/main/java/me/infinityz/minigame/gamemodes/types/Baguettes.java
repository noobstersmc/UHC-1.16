package me.infinityz.minigame.gamemodes.types;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.gamemodes.events.ArmorEquipEvent;
import me.infinityz.minigame.gamemodes.events.ArmorListener;
import me.infinityz.minigame.gamemodes.events.DispenserArmorListener;
import net.md_5.bungee.api.ChatColor;

/*
* 3/4 Diamond/Netherite Max
* Power 3 Max
*/
public class Baguettes extends IGamemode implements Listener {
    private ArmorListener armorListener = new ArmorListener();
    private DispenserArmorListener dispenserArmorListener = new DispenserArmorListener();
    private UHC instance;

    public Baguettes(UHC instance) {
        super("Baguettes", "Shadoune Rules oh yeah.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        // Listener to detect armor and stuff.
        instance.getListenerManager().registerListener(this);
        instance.getListenerManager().registerListener(armorListener);
        instance.getListenerManager().registerListener(dispenserArmorListener);

        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        // Disable the listeners that allow for this to work
        instance.getListenerManager().registerListener(this);
        instance.getListenerManager().unregisterListener(armorListener);
        instance.getListenerManager().unregisterListener(dispenserArmorListener);

        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onArmorEvent(ArmorEquipEvent e) {
        final var player = e.getPlayer();
        if (e.getNewArmorPiece() == null || e.getNewArmorPiece().getType() == Material.AIR
                || !isTopTier(e.getNewArmorPiece())) {
            return;
        }
        if (isMaxedOut(player)) {
            player.sendMessage(ChatColor.RED + "Your armor is maxed out. Cannot equip anything more.");
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent e) {
        e.getEnchantsToAdd().entrySet().forEach(this::nerfEnchantments);
    }

    @EventHandler
    public void prepareAnvilEvent(PrepareAnvilEvent e) {
        var result = e.getResult();
        if (result != null) {
            if (result.getType() == Material.ENCHANTED_BOOK) {
                var bookMeta = (EnchantmentStorageMeta) result.getItemMeta();
                if (bookMeta.getStoredEnchantLevel(Enchantment.ARROW_DAMAGE) > 3) {
                    e.setResult(null);
                }

            } else if (result.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) > 3) {
                e.setResult(null);
            }
        }
    }

    @EventHandler
    public void onPreEnchant(PrepareItemEnchantEvent e) {
        for (var offer : e.getOffers()) {
            if (offer == null || offer.getEnchantmentLevel() <= 3)
                continue;
            switch (offer.getEnchantment().getKey().getKey()) {
                case "power":
                    offer.setEnchantmentLevel(3);
                    break;
            }

        }
    }

    @EventHandler
    public void onLootGeneration(LootGenerateEvent e) {
        e.getLoot().forEach((stack) -> {
            if (stack != null) {
                if (stack.getType() == Material.ENCHANTED_BOOK) {
                    var bookMeta = (EnchantmentStorageMeta) stack.getItemMeta();
                    bookMeta.getStoredEnchants().entrySet().forEach(this::nerfEnchantments);
                } else if (stack.hasItemMeta()) {
                    stack.getEnchantments().entrySet().forEach(this::nerfEnchantments);
                }
            }
        });

    }

    private void nerfEnchantments(Map.Entry<Enchantment, Integer> entry) {
        if (entry.getValue() <= 3)
            return;

        var enchant = entry.getKey().getKey().getKey();

        switch (enchant) {
            case "power":
                entry.setValue(3);
                break;
        }
    }

    private boolean isMaxedOut(final Player player) {
        var inv = player.getInventory().getArmorContents();
        var topTierArmor = 0;
        // Count all the diamond or netherite pieces the player is wearing
        for (var armorPiece : inv)
            if (armorPiece != null && armorPiece.getType() != Material.AIR) {
                if (isTopTier(armorPiece))
                    topTierArmor++;
            }
        // Is three or more pieces are top tier, then the player is maxed out.
        return topTierArmor >= 3;
    }

    private boolean isTopTier(ItemStack stack) {
        final var armor = stack.getType().toString().toLowerCase();
        return armor.contains("diamond") || armor.contains("netherite");
    }

}
