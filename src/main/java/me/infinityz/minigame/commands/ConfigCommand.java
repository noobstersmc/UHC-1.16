package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.NetherDisabledEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * Config COMMAND
 */

@CommandAlias("config")
public @RequiredArgsConstructor class ConfigCommand extends BaseCommand {
    private @NonNull UHC instance;

    @Default
    public void configCMD(CommandSender sender) {
        var color = ChatColor.of("#5EA95F");
        var color2 = ChatColor.of("#776FC4");
        var color3 = ChatColor.of("#A40A0A") + "" + ChatColor.BOLD;
        var color4 = ChatColor.of("#BCBCBC");
        if (sender instanceof Player) {
            var player = (Player) sender;
            sender.sendMessage(color4
                    + "---------------------------------\n" + color + "Config: " + ChatColor.WHITE + "UHC "
                    + getGameType() + "\n");
            player.sendMessage(new ComponentBuilder("Scenarios: ").color(color)
                    .append(instance.getGamemodeManager().getScenariosWithDescription()).color(ChatColor.WHITE)
                    .create());
            sender.sendMessage(color2 + "PvP Enabled: " + ChatColor.WHITE + (instance.getGame().getPvpTime() / 60)
                    + "m\n" + color2 + "Border Time: " + ChatColor.WHITE + (instance.getGame().getBorderTime() / 60)
                    + "m\n" + color4 + "---------------------------------\n");

        } else {
            sender.sendMessage(color4 + "---------------------------------\n" + color3 + "        Noobsters\n" + color4
                    + "---------------------------------\n" + color + "Config: " + ChatColor.WHITE + "UHC "
                    + getGameType() + "\n" + color + "Scenarios: " + ChatColor.WHITE
                    + instance.getGamemodeManager().getEnabledGamemodesToString() + "\n" + color2 + "PvP Enabled: "
                    + ChatColor.WHITE + (instance.getGame().getPvpTime() / 60) + "m\n" + color2 + "Border Time: "
                    + ChatColor.WHITE + (instance.getGame().getBorderTime() / 60) + "m\n" + color4
                    + "---------------------------------\n");

        }

    }

    public String getGameType() {
        final int teamSize = instance.getTeamManger().getTeamSize();
        return teamSize > 1 ? "Teams of " + teamSize : "FFA";
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("nether")
    @CommandAlias("nether")
    @CommandCompletion("@bool")
    public void onNetherOff(CommandSender sender, @Optional Boolean bool) {
        if (bool != null) {
            instance.getGame().setNether(bool);
        } else {
            instance.getGame().setNether(!instance.getGame().isNether());
        }
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcastMessage(senderName + ChatColor.GREEN + "Nether has been set to " + instance.getGame().isNether());
        if (!instance.getGame().isNether()) {
            // Call Event
            Bukkit.getPluginManager().callEvent(new NetherDisabledEvent());
        }
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("tears-drop-gold")
    @CommandAlias("tears-drop-gold")
    @CommandCompletion("@bool")
    public void tearsNerf(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isTearsDropGold();

        instance.getGame().setTearsDropGold(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Tears drop gold has been set to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("deathmatch")
    @CommandAlias("deathmatch")
    @CommandCompletion("@bool")
    public void deathmatch(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isDeathMatch();

        instance.getGame().setDeathMatch(bool);
        if(bool == false && instance.getGame().isDeathMatchDamage()) instance.getGame().setDeathMatchDamage(false);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "DeathMatch has been set to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("anti-mining")
    @CommandAlias("anti-mining")
    @CommandCompletion("@bool")
    public void antimining(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isAntiMining();

        instance.getGame().setAntiMining(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Anti Mining has been set to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("privategame")
    @CommandAlias("privategame")
    @CommandCompletion("@bool")
    public void gamePrivate(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isPrivateGame();

        instance.getGame().setPrivateGame(bool);
        if(bool){
            Bukkit.dispatchCommand(sender, "whitelist add " + instance.getGame().getHostname());
            Bukkit.dispatchCommand(sender, "whitelist on");
        }else{
            Bukkit.dispatchCommand(sender, "whitelist off");
        }
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Private Game changed to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("max-disconnect")
    @CommandAlias("max-disconnect")
    public void changeMaxDisconectTime(CommandSender sender, Integer newMaxDisconnectTime) {
        instance.getGame().setMaxDisconnectTime(newMaxDisconnectTime*60);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Max Disconnect time changed to: " + newMaxDisconnectTime + " minutes.", "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("beds-nerf")
    @CommandAlias("beds-nerf")
    @CommandCompletion("@bool")
    public void changeBedsNerf(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isBedsNerf();

        instance.getGame().setBedsNerf(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Beds Nerf changed to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("advancements")
    @CommandAlias("advancements")
    @CommandCompletion("@bool")
    public void announceAdvancements(CommandSender sender, @Optional Boolean bool) {
        var game = instance.getGame();
        if (bool == null)
            bool = !game.isAdvancements();
        game.setAdvancements(bool);

        Bukkit.getWorlds().forEach(it -> {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, game.isAdvancements());
        });
        
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Show Advancements changed to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("potions")
    @CommandAlias("potions")
    @CommandCompletion("@bool")
    public void changePotions(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isPotions();

        instance.getGame().setPotions(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Potions has been set to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("strength-nerf")
    @CommandAlias("strength-nerf")
    @CommandCompletion("@bool")
    public void changeStrengthNerf(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isStrengthNerf();

        instance.getGame().setStrengthNerf(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Strength Nerf has been set to: " + bool, "uhc.configchanges.see");
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("apple-rate")
    @CommandAlias("apple-rate")
    public void changeApplerate(CommandSender sender, Double rate) {
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + 
                "Applerate has been changed from " + instance.getGame().getApplerate() + "% to " + rate + "%", "uhc.configchanges.see");
        instance.getGame().setApplerate(rate);
    }

    @CommandPermission("staff.perm")
    @Subcommand("setslots")
    @CommandAlias("slots||setslots||maxslots")
    public void changeSlots(CommandSender sender, Integer newSlots) {
        instance.getGame().setUhcslots(newSlots);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Slots set to: " + newSlots, "uhc.configchanges.see");
    }
    



}