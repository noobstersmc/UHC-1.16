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
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.events.CustomRecipeAddedEvent;
import me.infinityz.minigame.crafting.events.CustomRecipeRemovedEvent;
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
    private @Getter List<CustomRecipe> enabledRecipes = new ArrayList<>(); //all recipes that are actually enabled
    private @Getter HashMap<String, CustomRecipe> allRecipes = new HashMap<>(); //all recipes not necesary enabled
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

        goldCarrot = new CarrotRecipe(new NamespacedKey(instance, "golden_carrot"), null, "Golden Carrot");
        goldMelon = new MelonRecipe(new NamespacedKey(instance, "glistering_melon"), null, "Glistering Melon");
        goldenHead = new GoldenHead(new NamespacedKey(instance, "golden_head"), null, "Golden Head");

        Bukkit.addRecipe(goldCarrot.getRecipe());
        Bukkit.addRecipe(goldMelon.getRecipe());
        Bukkit.addRecipe(goldenHead.getRecipe());

        this.enabledRecipes.add(goldenHead);
        this.enabledRecipes.add(goldMelon);
        this.enabledRecipes.add(goldCarrot);

        //opcionales
        allRecipes.put("totem", new TotemRecipe(new NamespacedKey(instance, "totem"), null, "Totem"));
        allRecipes.put("dragon_breath", new DragonBreath(new NamespacedKey(instance, "dragon_breath"), null, "Dragon Breath"));
        allRecipes.put("trident", new TridentRecipe(new NamespacedKey(instance, "trident"), null, "Trident"));
        allRecipes.put("saddle", new SaddleRecipe(new NamespacedKey(instance, "saddle"), null, "Saddle"));
        allRecipes.put("krenzinator", new Krenzinator(new NamespacedKey(instance, "krenzinator"), null, "Krenzinator"));
        allRecipes.put("elytra", new ElytraRecipe(new NamespacedKey(instance, "elytra"), null, "Elytra"));
        allRecipes.put("notch", new NotchRecipe(new NamespacedKey(instance, "notch"), null, "Notch"));

        List<String> crafts = new ArrayList<>();
        allRecipes.keySet().forEach(key->{
            crafts.add(key);
        });

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("crafting", c -> {
            return crafts;
        });

        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @CommandAlias("crafting")
    public class CraftingCMD extends BaseCommand {

        @Default
        public void crafting(Player sender) {
            /* CRAFTING ENABLED GUI */
            instance.getGuiManager().getEnabledCraftingGui().open(sender);
        }

        @CommandPermission("crafting.cmd")
        @Subcommand("toggle")
        @CommandCompletion("@crafting")
        public void crafting(CommandSender sender, String craft) {
            if(allRecipes.containsKey(craft)){
                var customCraft = allRecipes.get(craft);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                if(!customCraft.isEnabled()){
                    customCraft.setEnabled(true);
                    Bukkit.addRecipe(customCraft.getRecipe());
                    Bukkit.getOnlinePlayers().forEach(all -> all.discoverRecipe(customCraft.getNamespacedKey()));
                    enabledRecipes.add(customCraft);
                    Bukkit.broadcast(senderName + ChatColor.YELLOW + "Craft "+ craft + " enabled.", "uhc.configchanges.see");

                    Bukkit.getPluginManager().callEvent(new CustomRecipeAddedEvent(customCraft));

                }else{
                    customCraft.setEnabled(false);
                    Bukkit.removeRecipe(customCraft.getNamespacedKey());
                    Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(customCraft.getNamespacedKey()));
                    removeCraft(craft);
                    Bukkit.broadcast(senderName + ChatColor.YELLOW + "Craft "+ craft + " disabled.", "uhc.configchanges.see");

                    Bukkit.getPluginManager().callEvent(new CustomRecipeRemovedEvent(customCraft));
                }
            }else{
                sender.sendMessage(ChatColor.RED + "Craft not available.");
            }
        }

    }

    private boolean removeCraft(String name){
        var iter = enabledRecipes.iterator();

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
        enabledRecipes.stream().map(CustomRecipe::getNamespacedKey).forEach(player::discoverRecipe);

    }

    public void purgeRecipes() {
        Bukkit.resetRecipes();
    }

    public void restoreRecipes(){
        enabledRecipes.stream().forEach(all->{
            Bukkit.getServer().addRecipe(all.getRecipe());
        });
    }

}