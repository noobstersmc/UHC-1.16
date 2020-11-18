package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.events.ScoreboardUpdateEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;


public class UHCMeetup extends IGamemode implements Listener {
    private UHC instance;
    private Random random = new Random();
    private WorldBorder worldBorder = Bukkit.getWorlds().get(0).getWorldBorder();
    private final ItemStack lapis = new ItemBuilder(Material.LAPIS_LAZULI).amount(64).build();
    private @Getter BukkitTask waitingForPlayers;
    private Integer amo = 0;

    public UHCMeetup(UHC instance) {
        super("UHC Meetup", "An UHC Meetup as a gamemode.");
        this.instance = instance;
        instance.getCommandManager().registerCommand(new reroll());
    }

    public void cancelWaitingForPlayers(){
        waitingForPlayers.cancel();
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);

        var newTittle = ChatColor.GREEN + "" + ChatColor.BOLD + "UHC Meetup";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
        Game.setScoreColors(ChatColor.of("#2cc36b") + "");

        instance.getGame().setNether(false);
        instance.getGame().setHealTime(-1);
        instance.getGame().setPvpTime(10);
        instance.getGame().setBorderTime(360);
        instance.getGame().setFinalBorderGrace(120);
        instance.getGame().setBorderCenterTime(120);
        instance.getGame().setBorderCenter(200);
        instance.getGame().setDMgrace(300);
        instance.getGame().setAntiMining(true);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bordersize 300");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobSpawning false ");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chat oi");
        
        waitingForPlayers = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            Bukkit.getOnlinePlayers().forEach(players -> {
                if(instance.getGameStage().equals(Stage.LOBBY) && isEnabled()) 
                    players.sendActionBar(ChatColor.GREEN + "Waiting for players...");
            });
        }, 5L, 20L);

        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);

        instance.getGame().setNether(true);
        instance.getGame().setPvpTime(1200);
        instance.getGame().setBorderTime(3600);
        instance.getGame().setFinalBorderGrace(300);
        instance.getGame().setBorderCenterTime(1800);
        instance.getGame().setBorderCenter(200);
        instance.getGame().setDMgrace(600);
        instance.getGame().setAntiMining(false);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bordersize 3000");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game score UHC");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobSpawning true ");

        cancelWaitingForPlayers();

        setEnabled(false);
        return true;
    }

    @Conditions("ingame")
    @CommandAlias("reroll||rl")
    public class reroll extends BaseCommand {

        @Default
        public void openBackPack(Player sender) {
            if(!isEnabled())
                sender.sendMessage(ChatColor.RED + "Command disabled.");

            else if(instance.getGame().getGameTime() > instance.getGame().getPvpTime())
                sender.sendMessage(ChatColor.RED + "ReRoll command available only at the start of the game.");

            else if(!sender.hasPermission("reroll.cmd"))
                sender.sendMessage(ChatColor.RED + "ReRoll command available only for special users. \n " 
                + ChatColor.GREEN + "Upgrade your rank at "+ ChatColor.GOLD + "noobsters.buycraft.net");

            else{
                sender.getInventory().clear();
                sender.setExp(0.0f);
                sender.setLevel(0);
                equip(sender);
            }
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(!instance.getGameStage().equals(Stage.LOBBY) || e.getPlayer().hasPlayedBefore()) return;
        var player = e.getPlayer();
        var world = Bukkit.getWorlds().get(0);
        var worldBorderSizeHaved = (int) world.getWorldBorder().getSize() / 2;
        player.teleportAsync(ChunksManager.findScatterLocation(world, worldBorderSizeHaved))
                .thenAccept(result -> player
                        .sendMessage((ChatColor.of("#7ab83c") + (result ? "You have been scattered into the world."
                                : "Coudn't scatter you, ask for help."))));     
    }

    @EventHandler
    public void onLobbyMove(PlayerMoveEvent e){
        if(!instance.getGameStage().equals(Stage.LOBBY)) return;
        var fromX = e.getFrom().getX();
        var fromZ = e.getFrom().getZ();
        var toX = e.getTo().getX();
        var toZ = e.getTo().getZ();
        if(fromX != toX || fromZ != toZ)
            e.setCancelled(true);
        
    }

    @EventHandler
    public void DisableAdvancements(PlayerAdvancementCriterionGrantEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(!instance.getGame().isPvp()) e.setCancelled(true);
    }

    @EventHandler
    public void onStart(GameTickEvent e) {
        if (e.getSecond() == 0) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                equip(players);
                players.sendMessage(ChatColor.of("#c3752c") + "Use /reroll to reload your kit!");
            });
        }
    }

    @EventHandler
    public void onPlayerJoinLate(PlayerJoinedLateEvent e){
        equip(e.getPlayer());
    }

    public void equip(Player player){
        player.setLevel(randomLevel(12));
        weaponItems(player);
        permaItems(player);
        misc(player);
        armor(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        var loc = e.getEntity().getPlayer().getLocation();
        loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(10);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final var player = e.getPlayer();
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            var lastDamage = (EntityDamageByEntityEvent) player.getLastDamageCause();
            var damager = lastDamage.getEntity();
            player.damage(1000, damager);
        } else {
            player.damage(1000);
        }
    }

    public void permaItems(Player player){
        final var inv = player.getInventory();

        inv.setItem(3, new ItemStack(Material.LAVA_BUCKET));
        if(random.nextBoolean()) inv.setItem(4, new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 2).build());
        else inv.setItem(4, new ItemBuilder(Material.IRON_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).build());
        inv.setItem(5, new ItemStack(Material.WATER_BUCKET));
        inv.setItem(6, specialItem());
        inv.setItem(7, new ItemStack(Material.GOLDEN_APPLE,randomLevel(4)+4));
        inv.setItem(8, new ItemStack(Material.OAK_PLANKS, 64));

        inv.addItem(new ItemStack(Material.IRON_INGOT, randomLevel(10)));
        inv.addItem(new ItemStack(Material.GOLD_INGOT, randomLevel(10)));
        inv.addItem(new ItemStack(Material.BOOK, randomLevel(3)));
        inv.addItem(new ItemStack(Material.ARROW, randomLevel(12)+12));
        inv.addItem(new ItemStack(Material.COBBLESTONE, 64));
        inv.addItem(new ItemStack(Material.ENCHANTING_TABLE));
        inv.addItem(new ItemStack(Material.LAVA_BUCKET));
        inv.addItem(new ItemStack(Material.WATER_BUCKET));

        if(random.nextBoolean()) inv.addItem(new ItemStack(Material.COOKED_BEEF, randomLevel(6)+6));
        else inv.addItem(new ItemStack(Material.COOKED_PORKCHOP, randomLevel(6)+6));

        switch(random.nextInt(2)){
            case 1:{
                inv.addItem(new ItemStack(Material.DAMAGED_ANVIL));
            }break;
            case 2:{
                inv.addItem(new ItemStack(Material.CHIPPED_ANVIL));
            }break;
            default:{
                inv.addItem(new ItemStack(Material.ANVIL));
            }break;
        }
    }

    public int randomLevel(Integer integer){
        var i = random.nextInt(integer);
        if(i == 0) return integer;
        else return i;
    }

    public void weaponItems(Player player){
        final var inv = player.getInventory();
        //SHIELD
        inv.setItemInOffHand(new ItemStack(Material.SHIELD));

        //SWORDS
        if(random.nextInt(20) == 0) inv.setItem(0, new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 2).enchant(Enchantment.FIRE_ASPECT).build());
        else if(random.nextBoolean()) inv.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, randomLevel(3)).build());
        else inv.setItem(0, new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, randomLevel(3)+1).build());

        //BOW
        if(random.nextInt(20) == 0)inv.setItem(1, new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_KNOCKBACK, 1).build());
        else if(random.nextInt(20) == 0) inv.setItem(1, new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_FIRE, 1).build());
        else inv.setItem(1, new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, randomLevel(2)).build());
        
        //AXES
        if(random.nextBoolean()) inv.setItem(2, new ItemStack(Material.DIAMOND_AXE));
        else inv.setItem(2, new ItemStack(Material.IRON_AXE));
    }

    public void misc(Player player){
        final var inv = player.getInventory();
        switch(random.nextInt(3)){
            case 1:{
                inv.addItem(new ItemStack(Material.PUFFERFISH_BUCKET));
            }break;
            case 2:{
                inv.addItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                inv.addItem(new ItemStack(Material.SMITHING_TABLE));
            }break;
            case 3:{
                inv.addItem(new ItemStack(Material.TNT, randomLevel(5)));
                inv.addItem(new ItemStack(Material.FLINT_AND_STEEL));
            }break;
            default:{
                inv.addItem(new ItemStack(Material.GRINDSTONE));
            }break;
        }

    }

    public ItemStack specialItem(){
        ItemStack special;
        switch(random.nextInt(9)){
            case 1:{
                special = new ItemStack(Material.ENDER_PEARL);
            }break;
            case 2:{
                special = new ItemStack(Material.COBWEB, randomLevel(4)+4);
            }break;
            case 3:{
                special = new ItemBuilder(Material.TRIDENT).enchant(Enchantment.LOYALTY, 2).enchant(Enchantment.IMPALING, 2).build();
            }break;
            case 4:{
                special = new ItemBuilder(Material.CROSSBOW).enchant(Enchantment.PIERCING, 2).build();
            }break;
            case 5:{
                special = new ItemStack(Material.SPECTRAL_ARROW, randomLevel(6)+6);
            }break;
            case 6:{
                special = PotionItemStack(Material.SPLASH_POTION, PotionType.INSTANT_DAMAGE, false, true);
            }break;
            case 7:{
                special = PotionItemStack(Material.POTION, PotionType.SPEED, false, false);
            }break;
            case 8:{
                special = PotionItemStack(Material.POTION, PotionType.FIRE_RESISTANCE, false, false);
            }break;
            case 9:{
                special = PotionItemStack(Material.LINGERING_POTION, PotionType.JUMP, false, false);
            }break;
            default:{
                special = PotionItemStack(Material.SPLASH_POTION, PotionType.SLOWNESS, false, false);
            }break;
        }
        return special;
    }

    public ItemStack PotionItemStack(Material type, PotionType potionTypeEffect, boolean extend, boolean upgraded){
        ItemStack potion = new ItemStack(type, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(potionTypeEffect, extend, upgraded));
        potion.setItemMeta(meta);
        return potion;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInterceptUpate(ScoreboardUpdateEvent e) {
        e.setCancelled(true);
    }
     

    @EventHandler(priority = EventPriority.HIGH)
    public void onModifyScoreboard(ScoreboardUpdateEvent e) {
        e.setCancelled(false);
        var player = e.getScoreboard().getPlayer();

        var uhcPlayer = instance.getPlayerManager().getPlayer(player.getUniqueId());

        e.setLinesArray(
                ChatColor.of("#2cc36b") + "Your Kills: " + ChatColor.WHITE + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
                "",
                ChatColor.of("#2cc36b") + "Players Left: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(),
                ChatColor.of("#2cc36b") + "Gamemode: " + ChatColor.WHITE + instance.getGamemodeManager().getFirstEnabledScenario(),
                "",
                ChatColor.of("#2cc36b") + "Border: " + ChatColor.WHITE + ((int) worldBorder.getSize() / 2), 
                "",
                ChatColor.WHITE + "noobsters.net");

    }

    @EventHandler
    public void openInventoryEvent(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, lapis);
        }
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, null);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        ItemStack item = e.getCurrentItem();
        if (inv instanceof EnchantingInventory) {

            if (item.getType().equals(lapis.getType())) {
                e.setCancelled(true);
            } else {
                e.getInventory().setItem(1, lapis);
            }
        }
    }

    public void armor(Player player){
        final var inv = player.getInventory();
        if(amo > 12) amo = 0;
        switch(amo){
            case 1:{
                
            inv.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
            inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                
            }break;
            case 2:{
             
            inv.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
      
            }break;
            case 3:{
              
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
                    
            }break;
            case 4:{
             
            inv.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_PROJECTILE).build());
            inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
                        
            }break;
            case 5:{
              
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
            inv.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build());
                
            }break;
            case 6:{
               
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
            inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build());
            inv.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
                                
            }break;
            case 7:{
              
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build());
            inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
                                    
            }break;
            case 8:{
              
            inv.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
                                        
            }break;
            case 9:{
               
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                                            
            }break;
            case 10:{
                
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                                                
            }break;
            case 11:{
               
            inv.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_PROJECTILE).build());
            inv.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
                                                
            }break;
            case 12:{
              
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
            inv.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
                                                    
            }break;
            default:{
     
            inv.setHelmet(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
            inv.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
            inv.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL).build());
                  
            }break;
        }
        amo ++;
    }
}

