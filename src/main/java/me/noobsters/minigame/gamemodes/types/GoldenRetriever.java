package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.mrmicky.fastinv.ItemBuilder;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class GoldenRetriever extends IGamemode implements Listener {
    private UHC instance;

    public GoldenRetriever(UHC instance) {
        super("GoldenRetriever", "Players drop Golden Heads.", Material.GOLDEN_APPLE);
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
    public void onDeath(PlayerDeathEvent e){
        ItemStack goldenHead = new ItemBuilder(Material.GOLDEN_APPLE).name(ChatColor.GOLD + "Golden Head").build();
        e.getDrops().add(goldenHead);

    }

}