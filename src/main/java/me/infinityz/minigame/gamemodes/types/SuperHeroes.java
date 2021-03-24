package me.infinityz.minigame.gamemodes.types;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class SuperHeroes extends IGamemode implements Listener {
    private UHC instance;
    private Random random = new Random();
    private String permissionDebug = "uhc.configchanges.see";

    public SuperHeroes(UHC instance) {
        super("SuperHeroes",
                "Players get random super power, each power controls a potion effect.", Material.BEACON);
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new SuperHeroesCMD());
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        instance.getGame().setPotions(false);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        instance.getGame().setPotions(true);
        setEnabled(false);
        return true;
    }

    @CommandPermission("uhc.scenarios")
    @CommandAlias("superheroes")
    public class SuperHeroesCMD extends BaseCommand {

        @Subcommand("give-powers")
        @CommandAlias("give-powers")
        public void givePowers(CommandSender sender) {

            Bukkit.getOnlinePlayers().forEach(player ->{
                givePower(player);
            });
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "Superpowers refreshed.", permissionDebug);
        }

        @Subcommand("clear-powers")
        @CommandAlias("clear-powers")
        public void clearPowers(CommandSender sender) {

            Bukkit.getOnlinePlayers().forEach(player ->{
                player.getActivePotionEffects().forEach(all -> player.removePotionEffect(all.getType()));
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            });
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "Superpowers cleared.", permissionDebug);
        }

    }

    public void givePower(Player player){
        switch(random.nextInt(9)) {
            case 1: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 100000, 0, true, false));
            }break;
            case 2: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 100000, 1, true, false));
            }break;
            case 3: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 100000, 1, true, false));
            }break;
            case 4: {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0); 
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 4, true, false));
            }break;
            case 5: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 100000, 0));
            }break;
            case 6: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 100000, 0, true, false));
            }break;
            case 7: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 100000, 0, true, false));
            }break;
            case 8: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 100000, 0, true, false));
            }break;
            default: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 100000, 2, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 100000, 0, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 100000, 0, true, false));

            }break;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinLate(PlayerJoinedLateEvent e){
        Bukkit.getScheduler().runTask(instance, () -> {
            givePower(e.getPlayer());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStart(GameStartedEvent e) {
        Bukkit.getScheduler().runTask(instance, () -> {
            Bukkit.getOnlinePlayers().forEach(players->{
                givePower(players);
            });
        });

    }


}