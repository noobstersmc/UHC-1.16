package me.infinityz.minigame.commands;

import java.text.DecimalFormat;

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

@CommandAlias("config|c")
public @RequiredArgsConstructor class ConfigCommand extends BaseCommand {
    private @NonNull UHC instance;
    private String permissionDebug = "uhc.configchanges.see";
    DecimalFormat numberFormat = new DecimalFormat("#0.0"); 

    @Default
    public void configCMD(CommandSender sender) {
        var gui = instance.getGuiManager().getMainGui();
        gui.open((Player) sender);

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
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Nether has been set to " + instance.getGame().isNether(), permissionDebug);
        if (!instance.getGame().isNether()) {
            // Call Event
            Bukkit.getPluginManager().callEvent(new NetherDisabledEvent());
        }
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("tears")
    @CommandAlias("tears")
    @CommandCompletion("@bool")
    public void tearsNerf(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isTears();

        instance.getGame().setTears(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Tears has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("deathmatch")
    @CommandAlias("deathmatch")
    @CommandCompletion("@bool")
    public void deathmatch(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isDeathMatch();

        instance.getGame().setDeathMatch(bool);
        if(bool == true && !instance.getGame().isDeathMatchDamage()) instance.getGame().setDeathMatchDamage(true);
        if(bool == false && instance.getGame().isDeathMatchDamage()) instance.getGame().setDeathMatchDamage(false);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "DeathMatch has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("privategame")
    @CommandAlias("privategame")
    @CommandCompletion("@bool")
    public void gamePrivate(CommandSender sender, @Optional Boolean bool) {
        var game = instance.getGame();
        if (bool == null)
            bool = !game.isPrivateGame();

        game.setPrivateGame(bool);
        game.setWhitelistEnabled(bool);

        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Game changed to: " + (bool ? "Private" : "Public"), permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("max-disconnect")
    @CommandAlias("max-disconnect")
    public void changeMaxDisconectTime(CommandSender sender, Integer newMaxDisconnectTime) {
        instance.getGame().setMaxDisconnectTime(newMaxDisconnectTime*60);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Max Disconnect time changed to: " + newMaxDisconnectTime + " minutes.", permissionDebug);
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
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Beds Nerf changed to: " + bool, permissionDebug);
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
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Show Advancements changed to: " + bool, permissionDebug);
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
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Potions has been set to: " + bool, permissionDebug);
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
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Strength Nerf has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("apple-rate")
    @CommandAlias("apple-rate")
    public void changeApplerate(CommandSender sender, Double rate) {
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        var from = numberFormat.format(instance.getGame().getAppleRate());
        var to = numberFormat.format(rate);
        Bukkit.broadcast(senderName + ChatColor.YELLOW + 
                "Applerate has been changed from " + from + "% to " + to + "%", permissionDebug);
        instance.getGame().setAppleRate(rate);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("flint-rate")
    @CommandAlias("flint-rate")
    public void changeFlintRate(CommandSender sender, Double rate) {
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        var from = numberFormat.format(instance.getGame().getFlintRate());
        var to = numberFormat.format(rate);
        Bukkit.broadcast(senderName + ChatColor.YELLOW + 
                "Flint Rate has been changed from " + from + "% to " + to + "%", permissionDebug);
        instance.getGame().setFlintRate(rate);
    }

    @CommandPermission("staff.perm")
    @Subcommand("setslots")
    @CommandAlias("slots||setslots||maxslots")
    public void changeSlots(CommandSender sender, Integer newSlots) {
        instance.getGame().setUhcSlots(newSlots);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Slots set to: " + newSlots, permissionDebug);
    }
    
    @CommandPermission("uhc.config.cmd")
    @Subcommand("beds")
    @CommandAlias("beds")
    @CommandCompletion("@bool")
    public void changeBeds(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isBeds();

        instance.getGame().setBeds(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Beds has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("trident")
    @CommandAlias("trident")
    @CommandCompletion("@bool")
    public void changeTrident(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isTrident();

        instance.getGame().setTrident(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Trident 100% drop has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("strength")
    @CommandAlias("strength")
    @CommandCompletion("@bool")
    public void changeStrength(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isStrength();

        instance.getGame().setStrength(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Strength has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("horses")
    @CommandAlias("horses")
    @CommandCompletion("@bool")
    public void changeHorses(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isHorses();

        instance.getGame().setHorses(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Horses has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("items-burn")
    @CommandAlias("items-burn")
    @CommandCompletion("@bool")
    public void changeItemsBurn(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isItemsBurn();

        instance.getGame().setItemsBurn(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Items burn has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("uhc.config.cmd")
    @Subcommand("trades")
    @CommandAlias("trades")
    @CommandCompletion("@bool")
    public void changeTrades(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isTrades();

        instance.getGame().setTrades(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Trades has been set to: " + bool, permissionDebug);
    }


}