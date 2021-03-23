package me.infinityz.minigame.gamemodes.types.guest;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.ScoreboardUpdateEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.tasks.GameLoop;
import net.md_5.bungee.api.ChatColor;

public class UHCGuest extends IGamemode implements Listener {
    private UHC instance;
    private WorldBorder worldBorder;

    public UHCGuest(UHC instance) {
        super("UHC Guest", "Custom features.");
        this.instance = instance;

    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;

        instance.getListenerManager().registerListener(this);

        String title = ChatColor.GOLD + "" + ChatColor.BOLD + "UHC " + ChatColor.of("#308242") + "" + ChatColor.BOLD
        + "MÉ" + ChatColor.of("#ddf3e2") + "" + ChatColor.BOLD + "XI" + ChatColor.of("#cb1014") + "" + ChatColor.BOLD + "CO";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + title);
        Game.setScoreColors(ChatColor.of("#77cb10") + "");

        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game score UHC");

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onModifyScoreboard(ScoreboardUpdateEvent e) {
        if (worldBorder == null)
            worldBorder = Bukkit.getWorld("world").getWorldBorder();

        e.setCancelled(false);
        var list = new ArrayList<String>();
        var player = e.getScoreboard().getPlayer();

        var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());

        if (team != null) {
            list.add(ChatColor.of("#77cb10") + "Tiempo: " + ChatColor.WHITE
                + GameLoop.timeConvert(instance.getGame().getGameTime()));
            list.add("");
            for (var members : team.getMembers()) {
                if (members != null
                        && members.getMostSignificantBits() != player.getUniqueId().getMostSignificantBits()) {
                    var offlinePlayer = Bukkit.getOfflinePlayer(members);
                    if (offlinePlayer.isOnline()) {
                        var onlineMember = offlinePlayer.getPlayer();
                        if (onlineMember.getGameMode() != GameMode.SPECTATOR) {
                            list.add(ChatColor.GREEN + onlineMember.getName() + String.format(
                                ChatColor.WHITE + " %.1f" + ChatColor.DARK_RED + "❤",
                                (double) Math.round(
                                        (onlineMember.getHealth() + onlineMember.getAbsorptionAmount()) / 2.0D)));

                        } else {
                            list.add(ChatColor.RED + "❌ " + ChatColor.STRIKETHROUGH + onlineMember.getName() + "");
                        }
                    } else {

                        var uhcPlayer = instance.getPlayerManager().getPlayer(members);
                        if (uhcPlayer != null && !uhcPlayer.isAlive()) {
                            list.add(ChatColor.RED + "❌ " + ChatColor.STRIKETHROUGH + offlinePlayer.getName() + "");
                        } else {
                            list.add(ChatColor.GRAY + "⌛ " + offlinePlayer.getName() + "");

                        }

                    }

                }
            }

        }
        list.add("");
        list.add(ChatColor.of("#77cb10") + "Borde: " + ChatColor.WHITE + ((int) worldBorder.getSize() / 2));
        list.add(ChatColor.of("#77cb10") + "Vivos: " + ChatColor.WHITE
                + instance.getPlayerManager().getAlivePlayers());
        list.add("");
        list.add(ChatColor.WHITE + "noobsters.net");

        e.setLines(list.toArray(new String[] {}));
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.GHAST_TEAR) {
            stack.setType(Material.GOLD_INGOT);
        }
    }

}