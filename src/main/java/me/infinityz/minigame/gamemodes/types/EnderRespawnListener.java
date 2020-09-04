package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

/**
 * EnderRespawnListener
 */
public class EnderRespawnListener implements Listener {

    public EnderRespawnListener(UHC instance) {
        class EnderRespawnRecipe extends CustomRecipe {
            public EnderRespawnRecipe() {
                super(new NamespacedKey(instance, "respawn_crystal"), null);

                final ItemStack respawnCrystal = new ItemStack(Material.END_CRYSTAL);
                final ItemMeta im = respawnCrystal.getItemMeta();
                im.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawn Crystal");
                respawnCrystal.setItemMeta(im);

                final ShapelessRecipe recipe = new ShapelessRecipe(getNamespacedKey(), respawnCrystal);
                recipe.addIngredient(2, Material.DIAMOND);
                recipe.addIngredient(2, Material.BONE);
                recipe.addIngredient(2, Material.LEATHER);
                recipe.addIngredient(1, Material.ENDER_PEARL);
                recipe.addIngredient(2, Material.GOLDEN_APPLE);

                setRecipe(recipe);
                Bukkit.getServer().addRecipe(getRecipe());
                instance.getCraftingManager().getRecipes().add(this);
            }

            @Override
            public void logic() {
                //

            }
        }
    }

    public void respawnAnimation(Location loc) {
        final var x = loc.getX();
        final var y = loc.getY();
        final var z = loc.getZ();
        final var tower = "fill %.0f %.0f %.0f %.0f %.0f %.0f minecraft:obsidian destroy";
        final var crystalCMD = "summon minecraft:end_crystal %f %f %f {BeamTarget:{X:%f,Y:%f,Z:%f}}";
        UHC.newChain().delay(1).sync(() -> {
            // TOWER 1
            loc.getWorld().setThundering(true);

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x - 17, y - 5, z - 1, x - 15, y + 15, z + 1));

        }).delay(20).sync(() -> {
            // TOWER 2

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x + 17, y - 5, z - 1, x + 15, y + 15, z + 1));

        }).delay(20).sync(() -> {
            // TOWER 3

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x - 1, y - 5, z - 17, x + 1, y + 15, z - 15));

        }).delay(20).sync(() -> {
            // TOWER 4

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x - 1, y - 5, z + 17, x + 1, y + 15, z + 15));

        }).delay(60).sync(() -> {
            // CRYSTAL 1
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(crystalCMD, x - 16, y + 16.5, z, x, y, z));

        }).delay(20).sync(() -> {
            // CRYSTAL 2
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(crystalCMD, x + 16, y + 16.5, z, x, y, z));

        }).delay(20).sync(() -> {
            // CRYSTAL 3
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(crystalCMD, x, y + 16.5, z - 16, x, y, z));

        }).delay(20).sync(() -> {
            // CRYSTAL 4
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(crystalCMD, x, y + 16.5, z + 16, x, y, z));

        }).delay(60).sync(() ->
        // LIGHTNING 1
        loc.getWorld().strikeLightning(loc.clone().set(x - 16, y + 16.5, z))

        ).delay(20).sync(() ->
        // LIGHTNING 2
        loc.getWorld().strikeLightning(loc.clone().set(x + 16, y + 16.5, z))

        ).delay(20).sync(() ->
        // LIGHTNING 3
        loc.getWorld().strikeLightning(loc.clone().set(x, y + 16.5, z - 16))

        ).delay(20).sync(() ->
        // LIGHTNING 4
        loc.getWorld().strikeLightning(loc.clone().set(x, y + 16.5, z + 16))

        ).delay(60).sync(() ->
        // CENTRAL LIGHTNING & RESPAWN
        loc.getWorld().strikeLightning(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10).sync(() -> {
                    loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z));
                    loc.getWorld().setThundering(false);
                }).sync(TaskChain::abort).execute();
    }

}