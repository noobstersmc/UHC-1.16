package me.infinityz.minigame.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.listeners.GlobalListener;
import me.infinityz.minigame.players.UHCPlayer;

public class ContextConditions {

    @SuppressWarnings("all")
    public ContextConditions(UHC instance) {

        instance.getCommandManager().getCommandContexts().registerIssuerAwareContext(UHCPlayer.class, context -> {
            if (!context.hasFlag("other")) {
                var issuer = context.getIssuer();
                var uhcp = instance.getPlayerManager().getPlayer(issuer.getUniqueId());
                if (uhcp == null)
                    throw new ConditionFailedException("Unkown user " + issuer.getIssuer().getName());
                return uhcp;
            } else {
                var firstArg = context.popFirstArg();
                if (firstArg != null && !firstArg.isEmpty()) {
                    var target = Bukkit.getOfflinePlayer(firstArg);
                    if (target != null) {
                        var uhcp = instance.getPlayerManager().getPlayer(target.getUniqueId());
                        if (uhcp != null) {
                            return uhcp;
                        }
                        throw new ConditionFailedException(firstArg + " is not an UHCPlayer");
                    } else {
                        throw new ConditionFailedException("Unknown user " + firstArg + ".");
                    }
                }
                throw new InvalidCommandArgument();
            }
        });
        instance.getCommandManager().getCommandConditions().addCondition(UHCPlayer.class, "alive",
                (context, executionContext, player) -> {
                    if (!player.isAlive())
                        throw new ConditionFailedException("You must be alive to do this");
                });
        instance.getCommandManager().getCommandConditions().addCondition(UHCPlayer.class, "spec",
                (context, executionContext, player) -> {
                    if (player.isAlive())
                        throw new ConditionFailedException("You must be an spectator to do this");
                });

        instance.getCommandManager().getCommandConditions().addCondition(UHCPlayer.class, "dead",
                (context, executionContext, player) -> {
                    if (player.isAlive())
                        throw new ConditionFailedException("You must not be alive to do this");
                });
        instance.getCommandManager().getCommandConditions().addCondition(UHCPlayer.class, "hasNotDied",
                (context, executionContext, player) -> {
                    if (player.isDead())
                        throw new ConditionFailedException(("You must not have died to do this."));
                });

        instance.getCommandManager().getCommandConditions().addCondition("ingame", (context) -> {
            if (!instance.gameStage.equals(Stage.INGAME))
                throw new ConditionFailedException(("Game must be playing!"));
        });

        instance.getCommandManager().getCommandConditions().addCondition("time", (c) -> {
            if (c.hasConfig("min") && c.getConfigValue("min", 0) > GlobalListener.time) {
                throw new ConditionFailedException(("Min value must be " + c.getConfigValue("min", 0)));
            }
            if (c.hasConfig("max") && c.getConfigValue("max", 3) < GlobalListener.time) {
                throw new ConditionFailedException(("Max value must be " + c.getConfigValue("max", 3)));
            }
        });
        // Add a condition to test for team management.
        instance.getCommandManager().getCommandConditions().addCondition("teamManagement", (context) -> {
            if (!instance.getTeamManger().isTeamManagement())
                throw new ConditionFailedException(("Team management is currently disabled!"));
        });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "hasTeam",
                (context, executionContext, player) -> {
                    if (instance.getTeamManger().getPlayerTeam(player.getUniqueId()) == null)
                        throw new ConditionFailedException("You must be in a team to do that");
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "hasNotTeam",
                (context, executionContext, player) -> {
                    if (instance.getTeamManger().getPlayerTeam(player.getUniqueId()) != null)
                        throw new ConditionFailedException("You must not be in a team to do that");
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "isTeamLeader",
                (context, executionContext, player) -> {
                    var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                    // Don't check is the team is null, hasTeam should do that for you
                    if (team != null && !team.isTeamLeader(player.getUniqueId()))
                        throw new ConditionFailedException("You must be the team leader to do that");
                });

        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "isNotTeamLeader",
                (context, executionContext, player) -> {
                    var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                    if (team != null) {
                        if (team.isTeamLeader(player.getUniqueId())) {
                            throw new ConditionFailedException(
                                    "You are the team leader. If you wish to disband your team, use /team disband");
                        }
                    } else {
                        throw new ConditionFailedException("You must be in a team to do that");
                    }
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "sameTeam",
                (context, executionContext, player) -> {
                    var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                    // Check if team is null
                    if (team == null)
                        throw new ConditionFailedException("Player is not in the same team.");
                    // Check if issuer is a player
                    if (context.getIssuer().getIssuer() instanceof Player) {
                        var issuer = context.getIssuer().getUniqueId();
                        // Check if issuer's team matches player's team
                        var teamIssuer = instance.getTeamManger().getPlayerTeam(issuer);
                        if (teamIssuer == null)
                            throw new ConditionFailedException("Player is not in the same team.");
                        // Compare the teamID.
                        if (teamIssuer.getTeamID().getMostSignificantBits() != team.getTeamID()
                                .getMostSignificantBits())
                            throw new ConditionFailedException("Player is not in the same team.");
                    }
                });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("bool", c -> {
            return ImmutableList.of("true", "false");
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("respawnArgs", c -> {
            return ImmutableList.of("--i", "-inventory", "--l", "-location");
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("otherplayers", c -> {
            if (c.getSender() instanceof Player) {
                return Bukkit.getOnlinePlayers().stream().filter(player -> player.getName() != c.getSender().getName())
                        .map(Player::getName).collect(Collectors.toList());
            }
            return null;
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("onlineplayers", c -> {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("uhcPlayers", c -> {
            return instance.getPlayerManager().getUhcPlayerMap().values().stream().map(uhcp -> Bukkit.getOfflinePlayer(uhcp.getUUID()).getName()).collect(Collectors.toList());
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("teamMembers", c -> {
            if (c.getSender() instanceof Player) {
                var team = instance.getTeamManger().getPlayerTeam(c.getIssuer().getUniqueId());
                if (team == null)
                    return null;
                return Arrays.asList(team.getMembers()).stream().filter(
                        uuid -> uuid.getMostSignificantBits() != c.getIssuer().getUniqueId().getMostSignificantBits())
                        .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.toList());
            }
            return null;
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("invites", c -> {
            if (c.getSender() instanceof Player) {
                var uuid = ((Player) c.getSender()).getUniqueId();
                return instance.getTeamManger().getTeamInvite().asMap().values().stream()
                        .filter(invite -> invite.getTarget().getMostSignificantBits() == uuid.getMostSignificantBits())
                        .map(invite -> Bukkit.getOfflinePlayer(invite.getSender()).getName())
                        .collect(Collectors.toList());
            }
            return null;
        });
    }

}