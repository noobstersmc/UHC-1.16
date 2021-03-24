package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.TeamWinEvent;
import me.infinityz.minigame.events.UHCPlayerDequalificationEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.listeners.IngameListeners;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.scoreboard.objects.FastBoard;
import me.infinityz.minigame.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

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
    private @Getter THashSet<UUID> molesPredefined = new THashSet<>();
    private final Random r = new Random();
    private final MolesCommand command;
    private boolean announceMoles = true;

    public Moles(UHC instance) {
        super("Moles", "One member of you team is secretly plotting against you.\nFind out who is it or die.", Material.IRON_SWORD);
        this.instance = instance;
        this.command = new MolesCommand();
        instance.getCommandManager().registerCommand(command);

    }

    @EventHandler
    public void onTeamWinEvent(TeamWinEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDequal(UHCPlayerDequalificationEvent e) {
        var uhcPlayer = e.getUhcPlayer();
        var isMole = isMole(uhcPlayer.getUUID());
        var playerTeam = instance.getTeamManger().getPlayerTeam(uhcPlayer.getUUID());
        if (playerTeam != null) {
            if (isMole != null) {
                isMole.sendTeamMessage(ChatColor.RED + "[MoleChat] " + e.getOfflinePlayer().getName() + " has died");
                if (announceMoles)
                    playerTeam.sendTeamMessage(ChatColor.RED + "" + e.getOfflinePlayer().getName() + " was the mole!");

            } else {
                if (announceMoles)
                    playerTeam.sendTeamMessage(
                            ChatColor.RED + "" + e.getOfflinePlayer().getName() + " was not the mole.");
            }

        }
        // Run in parallel thread
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {

            var moles = getAliveMoleTeams();
            var aliveTeams = new ArrayList<>(instance.getTeamManger().getAliveTeams());
            var alivePlayers = instance.getPlayerManager().getUhcPlayerMap().values().stream()
                    .filter(UHCPlayer::isAlive).collect(Collectors.toList());
            var playersNoTeam = alivePlayers.stream()
                    .filter(all -> instance.getTeamManger().getPlayerTeam(all.getUUID()) == null)
                    .collect(Collectors.toList());

            if (moles.isEmpty()) {
                if (aliveTeams.size() + playersNoTeam.size() == 1) {
                    // Call win for no moles
                }
            } else {
                for (int i = 0; i < aliveTeams.size(); i++) {
                    var team = aliveTeams.get(i);

                    var aliveMembers = team.getAliveMembers(instance);
                    int moleCount = 0;

                    for (var member : aliveMembers)
                        if (isMole(member.getUUID()) != null) {
                            moleCount++;
                            i++;
                        }

                    if (moleCount >= aliveMembers.size()) {
                        aliveTeams.remove(team);
                    }
                }
                if ((aliveTeams.size() + playersNoTeam.size()) <= 0 && moles.size() == 1) {
                    // Moles win

                }
            }

        });
    }

    public Collection<Team> getAliveMoleTeams() {
        return molesSet.stream()
                .filter(all -> all.getOfflinePlayersStream()
                        .map(uuid -> instance.getPlayerManager().getPlayer(uuid.getUniqueId()))
                        .anyMatch(UHCPlayer::isAlive))
                .collect(Collectors.toList());
    }

    public void giveMoleTag(Player player, Team t) {
        try {
            FastBoard.createTeam(player,
                    t.getOfflinePlayersStream()
                            .filter(member -> player.getUniqueId().getMostSignificantBits() != member.getUniqueId()
                                    .getMostSignificantBits())
                            .map(OfflinePlayer::getName).collect(Collectors.toList()),
                    "001Mole", t.getTeamPrefix(), 12);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        g(player);
    }

    void g(Player p) {
        var mole = isMole(p.getUniqueId());
        if (mole != null) {
            giveMoleTag(p, mole);
        }
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
        @CommandPermission("uhc.moles")
        public void onFindMoles(CommandSender sender) {
            if (!isEnabled())
                return;

            molesSet.clear();
            final StringBuilder builder = new StringBuilder();
            var moles = findMoles();
            createMoles(moles);
            sender.sendMessage(builder.toString());

            final List<UUID> molesUUID = moles.stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList());

            Bukkit.broadcastMessage(ChatColor.RED + "The Moles have been choosen.");
            Bukkit.getOnlinePlayers().forEach(players -> {
                try {
                    FastBoard.removeTeam(players, "001Mole");
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
                players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 1);
                var mole = false;
                for (UUID uuid : molesUUID) {
                    if (uuid.getMostSignificantBits() == players.getUniqueId().getMostSignificantBits()) {
                        try {
                            var team = isMole(players.getUniqueId());
                            if (team != null) {
                                players.sendMessage("You are the mole.");
                                giveMoleTag(players, team);
                                mole = true;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                if (!mole) {
                    players.sendMessage("You are not the mole.");
                }

            });

            sender.sendMessage("Moles: ");
            moles.forEach(all -> sender.sendMessage(" - " + all.getName()));
        }

        @Subcommand("add moles")
        @CommandCompletion("@onlineplayers")
        @CommandPermission("uhc.moles")
        public void addMole(CommandSender sender, @Flags("other") OfflinePlayer target) {
            if (addToMoles(target.getUniqueId())) {
                sender.sendMessage("Added " + target.getName() + " to moles");

            } else {
                sender.sendMessage("Coudln't add " + target.getName() + " to moles");
            }

        }

        @Subcommand("remove moles")
        @CommandCompletion("@onlineplayers")
        @CommandPermission("uhc.moles")
        public void removeMoles(CommandSender sender, @Flags("other") OfflinePlayer target) {
            if (target != null && molesPredefined.contains(target.getUniqueId())) {
                molesPredefined.remove(target.getUniqueId());
                sender.sendMessage("Removed " + target.getName());
            } else {
                sender.sendMessage("Couldn't remove that player.");
            }
        }

        @Subcommand("clear moles list")
        @CommandPermission("uhc.moles")
        public void clearMolesList(CommandSender sender) {
            molesPredefined.clear();
            sender.sendMessage("Clear Moles list.");
        }

        @Subcommand("list moles list")
        @CommandPermission("uhc.moles")
        public void listMolesList(CommandSender sender) {
            sender.sendMessage("Moles in list: ");
            molesPredefined.forEach(all -> {
                sender.sendMessage(" - " + Bukkit.getOfflinePlayer(all).getName());
            });
        }

        private boolean addToMoles(UUID uuid) {
            if (molesPredefined.contains(uuid)) {
                return false;
            }

            return molesPredefined.add(uuid);
        }

        @CommandAlias("choose")
        @CommandPermission("uhc.moles")
        public void chooseFromList(CommandSender sender) {
            var possibleMoles = getMolesPredefined();
            var size = instance.getTeamManger().getTeamSize() - 1;
            var iter = possibleMoles.iterator();
            int count = 0;

            while (iter.hasNext()) {
                var mole = iter.next();

                var team = new Team(mole);
                team.setTeamDisplayName("M" + count++);
                team.setTeamPrefix("☠ ");
                team.setTeamColorIndex(4);
                var player = Bukkit.getPlayer(mole);
                if (player != null && player.isOnline()) {

                    giveMoleTag(player, team);

                }
                molesSet.add(team);

                for (int i = 1; i <= size; i++) {
                    if (iter.hasNext()) {
                        i++;
                        var nextMole = iter.next();
                        team.addMember(nextMole);
                        var molePlayer = Bukkit.getPlayer(mole);
                        if (molePlayer != null && molePlayer.isOnline()) {
                            giveMoleTag(molePlayer, team);
                        }

                    }

                }
                team.sendTeamMessage(ChatColor.DARK_RED + "Your mole team is: ");
                team.getOfflinePlayersStream().forEach(c -> team.sendTeamMessage(c.getName()));
            }
            Bukkit.getOnlinePlayers().forEach(all -> {

                try {
                    FastBoard.removeTeam(all, "001Mole");
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
                g(all);
            });

        }

        @Subcommand("win")
        @CommandAlias("win")
        @CommandPermission("uhc.moles")
        public void winCommand(CommandSender sender, @Flags("other") OfflinePlayer target, String customMessage) {
            var mole = isMole(target.getUniqueId());
            var team = instance.getTeamManger().getPlayerTeam(target.getUniqueId());

            if (mole != null) {
                IngameListeners.playWinForTeam(mole, ChatColor.translateAlternateColorCodes('&', customMessage));
            } else if (team != null) {
                IngameListeners.playWinForTeam(team, ChatColor.translateAlternateColorCodes('&', customMessage));
            } else {
                sender.sendMessage("That player isn't mole or has no team");
            }

        }

        @Subcommand("tl|check")
        @CommandAlias("mtl")
        @CommandCompletion("@uhcPlayers")
        @CommandPermission("uhc.moles")
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

        @CommandAlias("announce")
        @CommandPermission("uhc.moles")
        public void onCommand(CommandSender sender, @Optional Boolean bool) {
            if (bool != null) {
                announceMoles = bool;
            } else {
                announceMoles = !announceMoles;
            }

            sender.sendMessage(ChatColor.of("#2be49c")
                    + (announceMoles ? "Mole Team Announce Enabled." : "Mole Team Announce Disabled."));

        }

        @Subcommand("chat")
        @CommandAlias("mc")
        public void moleChat(Player player, String message) {
            if (!isEnabled())
                return;
            var mole = isMole(player.getUniqueId());
            if (mole != null) {
                mole.sendTeamMessage(
                        ChatColor.RED + "[MoleChat] " + player.getName() + ": " + ChatColor.GRAY + "" + message);
            } else {
                player.sendMessage(ChatColor.RED + "You are not the mole.");
            }

        }

        @Subcommand("listall")
        @CommandPermission("uhc.moles")
        public void moleListall(Player player) {
            player.sendMessage("The moles are: ");
            molesSet.forEach(all -> {
                player.sendMessage("Moles " + all.getIdentifier() + all.getListOfMembers().toString());
            });

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
                for (int j = 1; j < teamSize; j++)
                    if (i >= 0) {
                        team.addMember(molesArray[i].getUniqueId());
                        i--;
                    }

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
