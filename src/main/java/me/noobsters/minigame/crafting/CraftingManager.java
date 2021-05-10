package me.noobsters.minigame.crafting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

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
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.crafting.events.CustomRecipeAddedEvent;
import me.noobsters.minigame.crafting.events.CustomRecipeRemovedEvent;
import me.noobsters.minigame.crafting.recipes.CarrotRecipe;
import me.noobsters.minigame.crafting.recipes.DragonBreath;
import me.noobsters.minigame.crafting.recipes.ElytraRecipe;
import me.noobsters.minigame.crafting.recipes.GoldenHead;
import me.noobsters.minigame.crafting.recipes.Krenzinator;
import me.noobsters.minigame.crafting.recipes.MelonRecipe;
import me.noobsters.minigame.crafting.recipes.NotchRecipe;
import me.noobsters.minigame.crafting.recipes.SaddleRecipe;
import me.noobsters.minigame.crafting.recipes.TotemRecipe;
import me.noobsters.minigame.crafting.recipes.TridentRecipe;
import net.md_5.bungee.api.ChatColor;

public class CraftingManager implements Listener {

    UHC instance;
    private @Getter HashMap<String, CustomRecipe> allRecipes = new HashMap<>(); //all recipes not necesary enabled

    public CraftingManager(UHC instance) {
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new CraftingCMD());
        instance.getServer().getPluginManager().registerEvents(this, instance);

        deleteRecipe(Material.GLISTERING_MELON_SLICE);
        deleteRecipe(Material.GOLDEN_CARROT);

        //default recipes

        CarrotRecipe goldCarrot = new CarrotRecipe(new NamespacedKey(instance, "golden_carrot"));
        MelonRecipe goldMelon = new MelonRecipe(new NamespacedKey(instance, "glistering_melon"));
        GoldenHead goldenHead = new GoldenHead(new NamespacedKey(instance, "golden_head"));

        goldCarrot.setEnabled(true);
        goldMelon.setEnabled(true);
        goldenHead.setEnabled(true);
        
        Bukkit.addRecipe(goldCarrot.getRecipe());
        Bukkit.addRecipe(goldMelon.getRecipe());
        Bukkit.addRecipe(goldenHead.getRecipe());

        allRecipes.put("Golden Carrot", goldCarrot);
        allRecipes.put("Glistering Melon", goldMelon);
        allRecipes.put("Golden Head", goldenHead);

        //opcionales
        allRecipes.put("Totem", new TotemRecipe(new NamespacedKey(instance, "totem")));
        allRecipes.put("Dragon Breath", new DragonBreath(new NamespacedKey(instance, "dragon_breath")));
        allRecipes.put("Trident", new TridentRecipe(new NamespacedKey(instance, "trident")));
        allRecipes.put("Saddle", new SaddleRecipe(new NamespacedKey(instance, "saddle")));
        allRecipes.put("Krenzinator", new Krenzinator(new NamespacedKey(instance, "krenzinator")));
        allRecipes.put("Elytra", new ElytraRecipe(new NamespacedKey(instance, "elytra")));
        allRecipes.put("Notch", new NotchRecipe(new NamespacedKey(instance, "notch")));

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("crafting", c -> {
            return allRecipes.keySet().stream().collect(Collectors.toList());
        });

    }

    @CommandAlias("crafting")
    public class CraftingCMD extends BaseCommand {

        @Default
        public void crafting(Player sender) {
            /* CRAFTING ENABLED GUI */
            instance.getGuiManager().getMainGui().getEnabledCraftingGui().open(sender);
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
                    Bukkit.broadcast(senderName + ChatColor.YELLOW + "Craft "+ craft + " enabled.", "uhc.configchanges.see");

                    Bukkit.getPluginManager().callEvent(new CustomRecipeAddedEvent(customCraft));

                }else{
                    customCraft.setEnabled(false);
                    Bukkit.removeRecipe(customCraft.getNamespacedKey());
                    Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(customCraft.getNamespacedKey()));
                    Bukkit.broadcast(senderName + ChatColor.YELLOW + "Craft "+ craft + " disabled.", "uhc.configchanges.see");

                    Bukkit.getPluginManager().callEvent(new CustomRecipeRemovedEvent(customCraft));
                }
            }else{
                sender.sendMessage(ChatColor.RED + "Craft not available.");
            }
        }

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

            var uuid = e.getPlayer().getUniqueId();
            var playerManager = instance.getPlayerManager();
            if(playerManager.getUhcPlayerMap().contains(uuid.getMostSignificantBits())){
                var player = playerManager.getUhcPlayerMap().get(uuid.getMostSignificantBits());
                player.setGoldenHeads(player.getGoldenHeads()+1);
            }
        }
    }

    @EventHandler
    public void onJoinGiveRecipe(PlayerJoinEvent e) {
        discoverCustomRecipes(e.getPlayer());
    }

    public void discoverCustomRecipes(Player player) {
        allRecipes.values().stream().filter(recipe-> recipe.isEnabled()).map(CustomRecipe::getNamespacedKey).forEach(player::discoverRecipe);

    }

    public void purgeRecipes() {
        Bukkit.resetRecipes();
    }

    public void restoreRecipes(){
        allRecipes.values().stream().filter(recipe-> recipe.isEnabled()).forEach(all->{
            Bukkit.getServer().addRecipe(all.getRecipe());
        });
    }

}