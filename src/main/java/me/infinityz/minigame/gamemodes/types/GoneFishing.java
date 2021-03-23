package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class GoneFishing extends IGamemode implements Listener {
    private UHC instance;
    ItemStack item = new ItemStack(Material.FISHING_ROD);

    public GoneFishing(UHC instance) {
        super("GoneFishing", "Go fishing! players start the game with a powerful fishing rod.");
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
    public void onStart(GameStartedEvent e) {
        var item = new ItemBuilder(Material.FISHING_ROD).enchant(Enchantment.LURE, 3).enchant(Enchantment.LUCK, 3).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.VANISHING_CURSE).build();
        Bukkit.getOnlinePlayers().forEach(players -> {
            players.getInventory().addItem(item);
        });
    }

    @EventHandler
    public void onPlayerJoinLate(PlayerJoinedLateEvent e){
        var item = new ItemBuilder(Material.FISHING_ROD).enchant(Enchantment.LURE, 3).enchant(Enchantment.LUCK, 3).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.VANISHING_CURSE).build();
        e.getPlayer().getInventory().addItem(item);
    }
    
    @EventHandler
    public void onFishing(PlayerFishEvent e){
        var player = e.getPlayer();
        if(e.getState() == State.FISHING && !player.hasPotionEffect(PotionEffectType.LUCK)){
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 20 * 100000, 200, true, false));
        }

    }



}