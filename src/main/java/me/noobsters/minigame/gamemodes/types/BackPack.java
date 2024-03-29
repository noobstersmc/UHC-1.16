package me.noobsters.minigame.gamemodes.types;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;
import me.noobsters.minigame.teams.objects.Team;

public class BackPack extends IGamemode implements Listener {
    private UHC instance;

    public BackPack(UHC instance) {
        super("BackPack", "Each team has a shared inventory using:\n/bp, /backpack, /ti, /teaminventory", Material.ENDER_CHEST);
        this.instance = instance;
        instance.getCommandManager().registerCommand(new backpack());
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled() || !instance.getTeamManger().isTeams())
            return false;
        instance.getListenerManager().registerListener(this);
        instance.getTeamManger().getTeamMap().values().forEach(Team::createTeamInventory);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled() || !instance.getTeamManger().isTeams())
            return false;
        instance.getTeamManger().getTeamMap().values().forEach(Team::destroyTeamInventory);
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        // inventory might required more than two chests on timebomb.
        var team = instance.getTeamManger().getPlayerTeam(e.getEntity().getUniqueId());
        if (team == null)
            return;
        if (team.getAliveMembers(instance).isEmpty()) {
            if (team.getTeamInventory() == null) {
                return;
            }
            team.getTeamInventory().forEach(all -> {
                if (all != null && all.getType() != Material.AIR)
                    e.getDrops().add(all);
            });
            team.destroyTeamInventory();
        }

    }

    @Conditions("ingame")
    @CommandAlias("backpack||bp||ti||teaminventory")
    public class backpack extends BaseCommand {

        @Default
        public void openBackPack(Player player) {
            if (isEnabled()) {
                var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                if (team == null) {
                    team = instance.getTeamManger().getTeamCommand().createTeam(player, "");
                    team.createTeamInventory();
                } else if (team.getTeamInventory() == null) {
                    team.createTeamInventory();
                }
                player.openInventory(team.getTeamInventory());
            } else {
                player.sendMessage(ChatColor.RED + "Backpack scenario is not enabled.");
            }
        }

    }

}