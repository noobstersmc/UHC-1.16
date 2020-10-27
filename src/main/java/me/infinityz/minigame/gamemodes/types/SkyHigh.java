package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class SkyHigh extends IGamemode implements Listener {
    private boolean damage = false;
    private UHC instance;

    public SkyHigh(UHC instance) {
        super("SkyHigh", "Players that stay below Y=150 periodically recieve damage after border time.");
        this.instance = instance;
        this.instance.getCommandManager().registerCommand(new SkyHighCMD());
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


    @EventHandler//condition damage
    public void onStart(GameTickEvent e) {
        if(e.getSecond() == instance.getGame().getBorderTime())
            damage = true;
        if (damage && e.getSecond() % 5 == 0) {
            Bukkit.getScheduler().runTask(instance, ()->{
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (players.getGameMode() == GameMode.SURVIVAL 
                        && players.getWorld().getEnvironment() != Environment.NETHER
                        && players.getLocation().getY() < 150)
                            players.damage(2);
                });
            });
        }
  
    }

    @CommandPermission("uhc.scenarios")
    @CommandAlias("skyhigh")
    public class SkyHighCMD extends BaseCommand {

        @Default
        public void toggleDamage(CommandSender sender) {
            damage = !damage;
            sender.sendMessage("Damage has been switch to: " + damage);
            Bukkit.broadcastMessage(ChatColor.of("#7fe5f0") + "Go above coordinate Y=150 now. Player's that remain in surface will take a heart of damage every 5 seconds.");

        }
    }

}
