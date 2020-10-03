package me.infinityz.minigame.gamemodes.types.uhclatam;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.NamespacedKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.Recipe;


import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.CustomRecipe;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class UHCLatam extends IGamemode implements Listener {
    private UHC instance;
    private @Getter List<CustomRecipe> recipes = new ArrayList<>();
    private NewCarrotRecipe newcarrotrecipe;
    private NewMelonRecipe newmelonrecipe;

    public UHCLatam(UHC instance) {
        super("UHC Latam", "T2");
        this.instance = instance;
        this.newcarrotrecipe = new NewCarrotRecipe(new NamespacedKey(instance, "newcarrotrecipe"), null);
        this.newmelonrecipe = new NewMelonRecipe(new NamespacedKey(instance, "newmelonrecipe"), null);
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
    
        instance.getListenerManager().registerListener(this);
        Bukkit.addRecipe(newcarrotrecipe.getRecipe());
        Bukkit.addRecipe(newmelonrecipe.getRecipe());
        Bukkit.getOnlinePlayers().forEach(all -> {
            all.discoverRecipe(this.newcarrotrecipe.getNamespacedKey());
            all.discoverRecipe(this.newmelonrecipe.getNamespacedKey());
        
        });
        
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        var count = 0;
            while (iter.hasNext() && count <2) {
                if (iter.next().getResult().getType() == Material.GOLDEN_CARROT 
                || iter.next().getResult().getType() == Material.GLISTERING_MELON_SLICE) {
                    iter.remove();
                    count++;
                }
            }

        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);

        Bukkit.removeRecipe(newcarrotrecipe.getNamespacedKey());
        Bukkit.removeRecipe(newmelonrecipe.getNamespacedKey());

        Bukkit.getOnlinePlayers().forEach(all -> {
            all.undiscoverRecipe(this.newcarrotrecipe.getNamespacedKey());
            all.undiscoverRecipe(this.newmelonrecipe.getNamespacedKey());
        
        });
        Bukkit.resetRecipes();
        instance.getCraftingManager().restoreRecipes();
        setEnabled(false);
        return true;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipe(this.newcarrotrecipe.getNamespacedKey());
        e.getPlayer().discoverRecipe(this.newmelonrecipe.getNamespacedKey());

    }

        
    @EventHandler
    public void onStart(GameTickEvent e) {
        if (e.getSecond() == 0) {
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "scoreboard objectives modify health_name rendertype hearts");
            });
        } else if(e.getSecond() == instance.getGame().getBorderTime()){
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
            });
        }
  
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.GHAST_TEAR) {
            stack.setType(Material.GOLD_INGOT);
        }
    }

    @EventHandler
    public void onBorderDamage(EntityDamageEvent e){
        if(e.getCause() == DamageCause.CUSTOM){
            e.setCancelled(true);
        }
    }
}   