package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

/**
 * EnderRespawn
 */
public class EnderRespawn extends IGamemode implements Listener {
    private UHC instance;
    private EnderRespawnRecipe recipe;

    public EnderRespawn(UHC instance) {
        super("Ender Respawn", "Respawn team leader with EnderCrystal.");
        this.instance = instance;
        this.recipe = new EnderRespawnRecipe(new NamespacedKey(instance, "respawn_crystal"), null);
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
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipe(this.recipe.getNamespacedKey());

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