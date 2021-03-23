package me.infinityz.minigame.crafting;

import java.util.ArrayList;
import java.util.HashMap;
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
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.recipes.CarrotRecipe;
import me.infinityz.minigame.crafting.recipes.DragonBreath;
import me.infinityz.minigame.crafting.recipes.ElytraRecipe;
import me.infinityz.minigame.crafting.recipes.GoldenHead;
import me.infinityz.minigame.crafting.recipes.Krenzinator;
import me.infinityz.minigame.crafting.recipes.MelonRecipe;
import me.infinityz.minigame.crafting.recipes.NotchRecipe;
import me.infinityz.minigame.crafting.recipes.SaddleRecipe;
import me.infinityz.minigame.crafting.recipes.TotemRecipe;
import me.infinityz.minigame.crafting.recipes.TridentRecipe;
import net.md_5.bungee.api.ChatColor;

public class CraftingManager implements Listener {

    UHC instance;
    private @Getter List<CustomRecipe> recipes = new ArrayList<>(); //all recipes that are actually enabled
    private @Getter HashMap<String, CustomRecipe> optionalRecipes = new HashMap<>(); //all recipes not necesary enabled
    //standar
    CarrotRecipe goldCarrot;
    MelonRecipe goldMelon;
    GoldenHead goldenHead;

    public CraftingManager(UHC instance) {
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new CraftingCMD());

        deleteRecipe(Material.GLISTERING_MELON_SLICE);
        deleteRecipe(Material.GOLDEN_CARROT);

        //default

        goldCarrot = new CarrotRecipe(new NamespacedKey(instance, "carrot"), null);
        goldMelon = new MelonRecipe(new NamespacedKey(instance, "melon"), null);
        goldenHead = new GoldenHead(new NamespacedKey(instance, "ghead"), null);

        Bukkit.addRecipe(goldCarrot.getRecipe());
        Bukkit.addRecipe(goldMelon.getRecipe());
        Bukkit.addRecipe(goldenHead.getRecipe());

        this.recipes.add(goldenHead);
        this.recipes.add(goldMelon);
        this.recipes.add(goldCarrot);

        //opcionales
        optionalRecipes.put("Totem", new TotemRecipe(new NamespacedKey(instance, "Totem"), null));
        optionalRecipes.put("Dragon_breath", new DragonBreath(new NamespacedKey(instance, "Dragon_breath"), null));
        optionalRecipes.put("Trident", new TridentRecipe(new NamespacedKey(instance, "Trident"), null));
        optionalRecipes.put("Saddle", new SaddleRecipe(new NamespacedKey(instance, "Saddle"), null));
        optionalRecipes.put("Krenzinator", new Krenzinator(new NamespacedKey(instance, "Krenzinator"), null));
        optionalRecipes.put("Elytra", new ElytraRecipe(new NamespacedKey(instance, "Elytra"), null));
        optionalRecipes.put("Notch", new NotchRecipe(new NamespacedKey(instance, "Notch"), null));

        List<String> crafts = new ArrayList<>();
        optionalRecipes.keySet().forEach(key->{
            crafts.add(key);
        });

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("crafting", c -> {
            return crafts;
        });

    }

    @CommandPermission("crafting.cmd")
    @CommandAlias("crafting")
    public class CraftingCMD extends BaseCommand {

        @Default
        @CommandCompletion("@crafting")
        public void crafting(CommandSender sender, String craft) {
            if(optionalRecipes.containsKey(craft)){
                var customCraft = optionalRecipes.get(craft);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                if(!customCraft.isEnabled()){
                    customCraft.setEnabled(true);
                    Bukkit.addRecipe(customCraft.getRecipe());
                    Bukkit.getOnlinePlayers().forEach(all -> all.discoverRecipe(customCraft.getNamespacedKey()));
                    recipes.add(customCraft);
                    Bukkit.broadcast(senderName + ChatColor.YELLOW + "Craft "+ craft + " enabled.", "uhc.configchanges.see");
                }else{
                    customCraft.setEnabled(false);
                    Bukkit.removeRecipe(customCraft.getNamespacedKey());
                    Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(customCraft.getNamespacedKey()));
                    removeCraft(craft);
                    Bukkit.broadcast(senderName + ChatColor.YELLOW + "Craft "+ craft + " disabled.", "uhc.configchanges.see");
                }
            }else{
                sender.sendMessage(ChatColor.RED + "Craft not available.");
            }
        }

    }

    private boolean removeCraft(String name){
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