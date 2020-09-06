package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.TeamWinEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.teams.objects.Team;

public class Moles extends IGamemode implements Listener {
    /**
     * - On Start of game, look at all teams and grab 1 member of each team and
     * collect those members. Them with those members, create teams of the same size
     * as the other ones. These new sub team members Also have a shared chat and
     * should be able to talk to each other. - For a player to win, they must be the
     * last team standing.
     */

    private UHC instance;
    private @Getter THashSet<Team> molesSet = new THashSet<>();
    private final Random r = new Random();
    private final MolesCommand command;

    public Moles(UHC instance) {
        super("Moles", "One member of you team is secretly plotting against you. \nFind out how it is or die.");
        this.instance = instance;
        this.command = new MolesCommand();
        instance.getCommandManager().registerCommand(command);

    }

    @EventHandler
    public void onTeamWinEvent(TeamWinEvent e){
        var aliveTeam = instance.getTeamManger().getAliveTeams();
        var aliveMoles = getAliveMoleTeams();
        if(aliveTeam.size() <= 1){
            if(aliveMoles.size() >= 1){
                Bukkit.broadcastMessage("Moles are still alive...");
                e.setCancelled(true);
            }
            
            
            
        }

    
    }
    public Collection<Team> getAliveMoleTeams() {
        return molesSet.stream()
                .filter(all -> all.getOfflinePlayersStream()
                        .map(uuid -> instance.getPlayerManager().getPlayer(uuid.getUniqueId()))
                        .anyMatch(UHCPlayer::isAlive))
                .collect(Collectors.toList());
    }
    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        setEnabled(true);
        instance.getListenerManager().registerListener(this);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        molesSet.clear();
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @CommandAlias("moles|mole")
    public class MolesCommand extends BaseCommand {

        @Subcommand("get")
        @CommandPermission("uhc.moles.create")
        public void onFindMoles(CommandSender sender) {
            if (!isEnabled())
                return;

            molesSet.clear();
            final StringBuilder builder = new StringBuilder();
            var moles = findMoles();
            findMoles().forEach(all -> builder.append(" - " + all.getName() + "\n"));
            createMoles(moles);
            sender.sendMessage(builder.toString());
            sender.sendMessage("Moles have been found.");

        }

        @Subcommand("tl|check")
        @CommandAlias("mtl")
        @CommandCompletion("@uhcPlayers")
        @CommandPermission("uhc.moles.check")
        public void moleCheck(CommandSender sender, @Flags("other") OfflinePlayer target) {
            if (!isEnabled())
                return;
            Bukkit.dispatchCommand(sender, "tl " + target.getName());
            var team = isMole(target.getUniqueId());
            if (team == null) {
                var targetsTeam = instance.getTeamManger().getPlayerTeam(target.getUniqueId());
                if (targetsTeam != null) {
                    targetsTeam.getMembersUUIDStream().forEach(all -> {
                        if (all.getMostSignificantBits() != target.getUniqueId().getMostSignificantBits()
                                && isMole(all) != null)
                            sender.sendMessage(Bukkit.getOfflinePlayer(all).getName() + " is the mole of the team");
                    });
                }
            } else {
                sender.sendMessage(target.getName() + " is a mole, his team mates are: ");
                team.getOfflinePlayersStream().forEach(all -> {
                    if (all.getUniqueId().getMostSignificantBits() != target.getUniqueId().getMostSignificantBits())
                        sender.sendMessage(" - " + all.getName());

                });
            }
        }

        @Subcommand("chat")
        @CommandAlias("mc")
        public void moleChat(Player player, String message){
            if (!isEnabled())
                return;
            var mole = isMole(player.getUniqueId());
            if(mole != null){
                mole.sendTeamMessage("[Mole][TeamChat] "+player.getName() + ": " + message);
            }else{
                player.sendMessage("You are not the mole.");
            }

        }

    }

    public Team isMole(UUID uuid) {
        for (var team : molesSet)
            if (team.isMember(uuid))
                return team;

        return null;
    }

    public void createMoles(Collection<OfflinePlayer> moles) {

        var teamSize = instance.getTeamManger().getTeamSize();
        var molesArray = moles.toArray(new OfflinePlayer[0]);

        for (int i = moles.size(); i >= 0; i--) {
            try {
                var leader = molesArray[i];
                var team = new Team(leader.getUniqueId());
                team.setTeamDisplayName("M" + i);
                molesSet.add(team);
                for (int j = 0; j < teamSize; j++)
                    if (i >= 0)
                        team.addMember(molesArray[i--].getUniqueId());
            } catch (Exception e) {// IGNORE
            }

        }

    }

    public Collection<OfflinePlayer> findMoles() {
        final ArrayList<OfflinePlayer> list = new ArrayList<>();
        instance.getTeamManger().getTeamMap().values().forEach(all -> {
            UUID[] uuid = all.getMembers();
            list.add(Bukkit.getOfflinePlayer(uuid[r.nextInt(uuid.length)]));
        });

        return list;
    }

}
