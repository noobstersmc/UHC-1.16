package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.advancements.AdvancementAPI;
import me.infinityz.minigame.advancements.FrameType;
import me.infinityz.minigame.advancements.Trigger;
import me.infinityz.minigame.advancements.Trigger.TriggerType;
import me.infinityz.minigame.crafting.CustomRecipe;
import me.infinityz.minigame.crafting.recipes.CarrotRecipe;
import me.infinityz.minigame.crafting.recipes.MelonRecipe;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.events.ScoreboardUpdateEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.tasks.GameLoop;
import net.md_5.bungee.api.ChatColor;

public class UHCLatam extends IGamemode implements Listener {
    private UHC instance;
    private @Getter List<CustomRecipe> recipes = new ArrayList<>();
    private WorldBorder worldBorder = Bukkit.getWorlds().get(0).getWorldBorder();
    private AdvancementAPI advancement;
    private CarrotRecipe carrotRecipe;
    private MelonRecipe melonRecipe;

    public UHCLatam(UHC instance) {
        super("UHC Latam", "T2");
        this.instance = instance;
        this.carrotRecipe = new CarrotRecipe(new NamespacedKey(instance, "newcarrotrecipe"), null);
        this.melonRecipe = new MelonRecipe(new NamespacedKey(instance, "newmelonrecipe"), null);
        this.advancement = AdvancementAPI.builder(new NamespacedKey(instance, "uhc-latam")).frame(FrameType.CHALLENGE)
                .background("minecraft:textures/block/blackstone_top.png").icon("golden_apple").title("¡UHC Latam T2!")
                .description("Participa en UHC Latam T2").toast(true)
                .trigger(Trigger.builder(TriggerType.IMPOSSIBLE, "hello")).announce(true).build();
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;

        instance.getListenerManager().registerListener(this);
        carrotRecipe.logic();
        melonRecipe.logic();
        Bukkit.getOnlinePlayers().forEach(all -> {
            all.discoverRecipe(this.carrotRecipe.getNamespacedKey());
            all.discoverRecipe(this.melonRecipe.getNamespacedKey());

        });

        Game.setScoreboardTitle(ChatColor.of("#E6E6FA") + "🗡 " + ChatColor.of("#77DBD6") + "" + ChatColor.BOLD
                + "UHC Latam T2 " + ChatColor.of("#E6E6FA") + "☠");

        advancement.add();

        Iterator<Recipe> iter = Bukkit.recipeIterator();
        var count = 0;
        while (iter.hasNext() && count < 2) {
            if (iter.next().getResult().getType() == Material.GOLDEN_CARROT
                    || iter.next().getResult().getType() == Material.GLISTERING_MELON_SLICE) {
                iter.remove();
                count++;
            }
        }

        setEnabled(true);
        return true;
    }

    /*
     * Scoreboard Interceptor ends
     */

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        advancement.remove();

        Bukkit.removeRecipe(carrotRecipe.getNamespacedKey());
        Bukkit.removeRecipe(melonRecipe.getNamespacedKey());

        Bukkit.getOnlinePlayers().forEach(all -> {
            all.undiscoverRecipe(this.carrotRecipe.getNamespacedKey());
            all.undiscoverRecipe(this.melonRecipe.getNamespacedKey());

        });
        Bukkit.resetRecipes();
        instance.getCraftingManager().restoreRecipes();
        setEnabled(false);
        return true;

    }


    /*
     * Scoreboard Interceptor starts
     */

    @EventHandler(priority = EventPriority.LOW)
    public void onInterceptUpate(ScoreboardUpdateEvent e) {
        e.setCancelled(true);
    }
    /*
     * 
     * ✘ UHC LATAM ☠ AQUA ➟ Episodio: 1
     * 
     * GREEN CrisGreen (-700, ) 9.5❤ GREEN Sparta GRAY(WHITE-951, 1344 GRAY) WHITE
     * 10 DARK RED❤
     * 
     * PINK Tiempo: WHITE 00:00:00
     * 
     * AWUA noobsters.net
     *
     * 
     * 
     */

    @EventHandler(priority = EventPriority.HIGH)
    public void onModifyScoreboard(ScoreboardUpdateEvent e) {
        e.setCancelled(false);
        var list = new ArrayList<String>();
        var player = e.getScoreboard().getPlayer();


        var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
        // https://papermc.io/javadocs/paper/1.16/org/bukkit/event/inventory/PrepareItemCraftEvent.html
        if (team != null) {
            list.add(ChatColor.of("#66CDAA") + "➟Equipo: ");
            list.add("");
            for (var members : team.getMembers()) {
                if (members != null
                        && members.getMostSignificantBits() != player.getUniqueId().getMostSignificantBits()) {
                    var offlinePlayer = Bukkit.getOfflinePlayer(members);
                    if (offlinePlayer.isOnline()) {
                        var onlineMember = offlinePlayer.getPlayer();
                        var location = onlineMember.getLocation();
                        var x = location.getBlockX();
                        var z = location.getBlockZ();
                        if (onlineMember.getGameMode() != GameMode.SPECTATOR) {
                            list.add(ChatColor.GREEN + onlineMember.getName() + "");
                            list.add(String.format(
                                    ChatColor.GRAY + "(" + ChatColor.of("#E6E6FA") + x + ", " + z + ChatColor.GRAY
                                            + ") " + ChatColor.of("#E6E6FA") + "%.1f" + ChatColor.DARK_RED + "❤",
                                    (double)Math.round((onlineMember.getHealth() + onlineMember.getAbsorptionAmount()) / 2.0D)));

                        } else {
                            list.add(ChatColor.RED + "☠ " + ChatColor.STRIKETHROUGH + onlineMember.getName() + "");
                        }
                    } else {

                        var uhcPlayer = instance.getPlayerManager().getPlayer(members);
                        if(uhcPlayer != null && !uhcPlayer.isAlive()){
                            list.add(ChatColor.RED + "☠ " + ChatColor.STRIKETHROUGH + offlinePlayer.getName() + "");
                        }else{
                            list.add(ChatColor.GREEN + offlinePlayer.getName() + "");
                            list.add(ChatColor.GRAY + "" + ChatColor.ITALIC + " Offline");
                            
                        }

                    }

                }
            }

        }
        list.add("");
        list.add(ChatColor.of("#66CDAA") + "➟Borde: " + ChatColor.of("#E6E6FA") + ((int) worldBorder.getSize() / 2));
        list.add(ChatColor.of("#66CDAA") + "➟Jugadores: " + ChatColor.of("#E6E6FA") + instance.getPlayerManager().getAlivePlayers());
        list.add(ChatColor.of("#66CDAA") + "➟Tiempo: " + ChatColor.of("#E6E6FA")
                + GameLoop.timeConvert(instance.getGame().getGameTime()));
        list.add("");
        list.add(ChatColor.of("#E6E6FA") + "noobsters.net");
        /*
         * e.setLinesArray(ChatColor.of("#66CDAA") + "➟Equipo: " + ChatColor.WHITE +
         * "1", "", ChatColor.GREEN + " CrisGreen", ChatColor.GRAY + "(" +
         * ChatColor.of("#E6E6FA") + "-700, -1920" + ChatColor.GRAY + ") " +
         * ChatColor.WHITE + "9.5" + ChatColor.DARK_RED + "❤", ChatColor.GREEN +
         * " Sparta", ChatColor.GRAY + "(" + ChatColor.of("#E6E6FA") + "-951, 1344" +
         * ChatColor.GRAY + ") " + ChatColor.WHITE + "10" + ChatColor.DARK_RED + "❤",
         * "", ChatColor.of("#FF69B4") + "➟Tiempo: " + ChatColor.WHITE + "00:00:00", "",
         * ChatColor.of("#E6E6FA") + "noobsters.net");
         */

        e.setLines(list.toArray(new String[] {}));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipe(this.carrotRecipe.getNamespacedKey());
        e.getPlayer().discoverRecipe(this.melonRecipe.getNamespacedKey());

    }

    @EventHandler
    public void onStart(GameTickEvent e) {
        if (e.getSecond() == 0) {
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "scoreboard objectives modify health_name rendertype hearts");
            });
        } else if (e.getSecond() == instance.getGame().getBorderTime()) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                players.setHealth(players.getHealth()+20.0);
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
    public void onBorderDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.CUSTOM) {
            e.setCancelled(true);
        }
    }

    // no funciona
    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent e) {
        var item = e.getItem().getType();
        System.out.println("entry");
        if (item == Material.AIR || item != Material.SUSPICIOUS_STEW || !e.getItem().hasItemMeta())
            return;
        System.out.println("check 1");
        SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) e.getItem().getItemMeta();
        if (stewMeta.hasCustomEffect(PotionEffectType.REGENERATION)) {
            stewMeta.clearCustomEffects();
            stewMeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 1), true);
            var itemConsumed = e.getItem();
            itemConsumed.setItemMeta(stewMeta);
            e.setItem(itemConsumed);
            var hasRegen = e.getPlayer().hasPotionEffect(PotionEffectType.REGENERATION);
            if (!hasRegen) {
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    e.getPlayer().addPotionEffect(PotionEffectType.REGENERATION.createEffect(1, 0));
                }, 1L);
            }
        }
    }

}