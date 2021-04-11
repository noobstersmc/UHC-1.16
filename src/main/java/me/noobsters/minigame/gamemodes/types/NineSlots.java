package me.noobsters.minigame.gamemodes.types;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.events.GameStartedEvent;
import me.noobsters.minigame.events.PlayerJoinedLateEvent;
import me.noobsters.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class NineSlots extends IGamemode implements Listener {
    private UHC instance;
    private ItemStack fillItem;

    public NineSlots(UHC instance) {
        super("NineSlots", "Only the hotbar slots are enabled.", Material.BARRIER);
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        fillItem = new ItemStack(Material.BARRIER);
        var meta = fillItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "BLOCKED");
        fillItem.setItemMeta(meta);
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
    public void onStart(GameStartedEvent e) {
        Bukkit.getScheduler().runTask(instance, () -> {
            Bukkit.getOnlinePlayers().forEach(players->{
                fillInventory(players);
            });
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        ItemStack item = e.getCurrentItem();

        if (item == null){
            return;
        }

        if (item.equals(fillItem)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        List<ItemStack> drops = e.getDrops();

        while (drops.remove(fillItem)){}
    }

    public void fillInventory(Player player){
        for (int i = 9; i <= 35; i++) {
            player.getInventory().setItem(i, fillItem);
        }
    }

    @EventHandler
    public void onPlayerJoinLate(PlayerJoinedLateEvent e){
        Bukkit.getScheduler().runTask(instance, ()->{
            fillInventory(e.getPlayer());

        });
    }



}