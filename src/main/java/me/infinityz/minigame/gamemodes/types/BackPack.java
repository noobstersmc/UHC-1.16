package me.infinityz.minigame.gamemodes.types;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.teams.objects.Team;

public class BackPack extends IGamemode implements Listener {
    private UHC instance;

    public BackPack(UHC instance) {
        super("BackPack", "Teams have a shared inventory.");
        this.instance = instance;
        instance.getCommandManager().registerCommand(new backpack());
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        instance.getTeamManger().getTeamMap().values().forEach(Team::createTeamInventory);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getTeamManger().getTeamMap().values().forEach(Team::destroyTeamInventory);
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @CommandAlias("backpack||bp||ti||teaminventory")
    public class backpack extends BaseCommand {

        @Default
        public void openBackPack(Player player) {
            var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
            if (team == null) {
                team = instance.getTeamManger().getTeamCommand().createTeam(player, "");
                team.createTeamInventory();
            }else if(team.getTeamInventory() == null){
                team.createTeamInventory();
            }
            player.openInventory(team.getTeamInventory());
        }

    }

}