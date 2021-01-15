package me.infinityz.minigame.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.recipes.CarrotRecipe;
import me.infinityz.minigame.crafting.recipes.DragonBreath;
import me.infinityz.minigame.crafting.recipes.GoldenHead;
import me.infinityz.minigame.crafting.recipes.GoldenHoney;
import me.infinityz.minigame.crafting.recipes.MelonRecipe;
import me.infinityz.minigame.crafting.recipes.NetheriteRecipe;
import me.infinityz.minigame.crafting.recipes.SimpleNetherite;
import me.infinityz.minigame.crafting.recipes.SuperGoldenHoney;
import me.infinityz.minigame.crafting.recipes.TotemRecipe;
import me.infinityz.minigame.crafting.recipes.UltraGoldenHoney;
import net.md_5.bungee.api.ChatColor;

public class CraftingManager implements Listener {

    UHC instance;
    private @Getter List<CustomRecipe> recipes = new ArrayList<>();
    TotemRecipe totem;
    CarrotRecipe goldCarrot;
    MelonRecipe goldMelon;
    DragonBreath dragonBreath;
    SimpleNetherite simpleNetherite; //netherite of 2
    NetheriteRecipe netheriteRecipe; //netherite of 4


    public CraftingManager(UHC instance) {
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new CraftingCMD());

        deleteRecipe(Material.GLISTERING_MELON_SLICE);
        deleteRecipe(Material.GOLDEN_CARROT);
        
        //opcionales
        totem = new TotemRecipe(new NamespacedKey(instance, "totem"), null);
        dragonBreath = new DragonBreath(new NamespacedKey(instance, "dragon_breath"), null);

        //default
        goldCarrot = new CarrotRecipe(new NamespacedKey(instance, "carrot"), null);
        goldCarrot.logic();
        goldMelon = new MelonRecipe(new NamespacedKey(instance, "melon"), null);
        goldMelon.logic();

        this.recipes.add(new GoldenHead(new NamespacedKey(instance, "ghead")));
        this.recipes.add(goldMelon);
        this.recipes.add(goldCarrot);

    }

    private boolean isRegisted(String name){
        var iter = recipes.iterator();

            while (iter.hasNext()) {
                
                if (iter.next().getNamespacedKey().getKey() == name) {
                    iter.remove();
                    return true;
                }
            }
        return false;
    }

    //deleting recipe before adding something with same result
    private void deleteRecipe(Material material){
        Iterator<Recipe> iter = Bukkit.recipeIterator();

        while (iter.hasNext()) {
                
            if (iter.next().getResult().getType() == material) {
                iter.remove();
                break;
            }
        }
    }

    //TODO: hacer sistema autocompletando con una lista
    @CommandPermission("crafting.cmd")
    @CommandAlias("crafting")
    public class CraftingCMD extends BaseCommand {

        @Subcommand("Totem")
        @CommandAlias("Totem")
        public void totemCraft(CommandSender sender) {
            if(!isRegisted("totem")){
                recipes.add(totem);
                Bukkit.addRecipe(totem.getRecipe());
                Bukkit.getOnlinePlayers().forEach(all -> all.discoverRecipe(totem.getNamespacedKey()));
                sender.sendMessage("Totem Craft Enabled.");
            }else{
                Bukkit.removeRecipe(totem.getNamespacedKey());
                Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(totem.getNamespacedKey()));
                sender.sendMessage("Totem Craft Disabled.");
            }
        }

        @Subcommand("DragonBreath")
        @CommandAlias("DragonBreath")
        public void dragonBreath(CommandSender sender) {
            if(!isRegisted("dragon_breath")){
                recipes.add(dragonBreath);
                Bukkit.addRecipe(dragonBreath.getRecipe());
                Bukkit.getOnlinePlayers().forEach(all -> all.discoverRecipe(dragonBreath.getNamespacedKey()));
                sender.sendMessage("Dragon Breath Craft Enabled.");
            }else{
                Bukkit.removeRecipe(dragonBreath.getNamespacedKey());
                Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(dragonBreath.getNamespacedKey()));
                sender.sendMessage("Dragon Breath Craft Disabled.");
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent e) {
        var item = e.getItem().getType();
        if (item == Material.AIR || item != Material.GOLDEN_APPLE || !e.getItem().hasItemMeta())
            return;
        ItemMeta itemMeta = e.getItem().getItemMeta();
        if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Golden Head")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
        }
    }

    @EventHandler
    public void onJoinGiveRecipe(PlayerJoinEvent e) {
        discoverCustomRecipes(e.getPlayer());
    }

    public void discoverCustomRecipes(Player player) {
        recipes.stream().map(CustomRecipe::getNamespacedKey).forEach(player::discoverRecipe);

    }

    public void purgeRecipes() {
        Bukkit.resetRecipes();
    }

    public void restoreRecipes(){
        recipes.stream().forEach(all->{
            Bukkit.getServer().addRecipe(all.getRecipe());
        });
    }

}