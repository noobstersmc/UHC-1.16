package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.event.player.PlayerLoginEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 *Config COMMAND
 */

@CommandAlias("config")
public @RequiredArgsConstructor class ConfigCommand extends BaseCommand {
    private @NonNull UHC instance;
    
    @Default
    public void configCMD(CommandSender sender){
        var color = ChatColor.of("#5EA95F");
        var color2 = ChatColor.of("#776FC4");
        var color3 = ChatColor.of("#A40A0A") + "" + ChatColor.BOLD;
        var color4 = ChatColor.of("#BCBCBC");
    if(sender instanceof Player){
        var player = (Player) sender;
        sender.sendMessage(color4 +"---------------------------------\n"
        + color3 + "        Noobsters\n"
        + color4 +"---------------------------------\n"
        + color + "Config: " + ChatColor.WHITE + "UHC " + getGameType() + "\n");
        player.sendMessage(new ComponentBuilder("Scenarios: ").color(color).append(instance.getGamemodeManager().getScenariosWithDescription()).color(ChatColor.WHITE).create());
        sender.sendMessage(
        color2 + "PvP Enabled: " + ChatColor.WHITE + (instance.getGame().getPvpTime() / 60)+ "m\n"
        + color2 + "Border Time: " + ChatColor.WHITE + (instance.getGame().getBorderTime() / 60) + "m\n"
        + color4 +"---------------------------------\n");

    }else{
        sender.sendMessage(color4 +"---------------------------------\n"
                                            + color3 + "        Noobsters\n"
                                            + color4 +"---------------------------------\n"
                                            + color + "Config: " + ChatColor.WHITE + "UHC " + getGameType() + "\n" 
                                            + color + "Scenarios: " +  ChatColor.WHITE + instance.getGamemodeManager().getEnabledGamemodesToString() +"\n"
                                            + color2 + "PvP Enabled: " + ChatColor.WHITE + (instance.getGame().getPvpTime() / 60)+ "m\n"
                                            + color2 + "Border Time: " + ChatColor.WHITE + (instance.getGame().getBorderTime() / 60) + "m\n"
                        + color4 +"---------------------------------\n");

    }
    
    }

    public String getGameType(){ 
        final int teamSize = instance.getTeamManger().getTeamSize();
        return teamSize > 1 ? "Teams of " + teamSize : "FFA";
    }

    @CommandPermission("uhc.config.pvp")
    @Subcommand("pvptime")
    @CommandAlias("pvptime")
    public void changePvpTime(CommandSender sender, Integer newPvpTime) {
        instance.getGame().setPvpTime(newPvpTime*60);
        sender.sendMessage("Pvp time changed to " + newPvpTime + " minutes.");
    }

    @CommandPermission("uhc.config.heal")
    @Subcommand("healtime")
    @CommandAlias("healtime")
    public void changeHealTime(CommandSender sender, Integer newHealTime) {
        instance.getGame().setHealTime(newHealTime*60);
        sender.sendMessage("Heal time changed to " + newHealTime + " minutes.");
    }

    @CommandPermission("uhc.config.border")
    @Subcommand("bordertime")
    @CommandAlias("bordertime")
    public void changeBorderTime(CommandSender sender, Integer newBorderTime) {
        instance.getGame().setBorderTime(newBorderTime*60);
        sender.sendMessage("Border time changed to " + newBorderTime + " minutes.");
    }

    @CommandPermission("uhc.config.border")
    @Subcommand("bordercentertime")
    @CommandAlias("bordercentertime")
    public void changeBorderCenterTime(CommandSender sender, Integer newBorderCenterTime) {
        instance.getGame().setBorderCenterTime(newBorderCenterTime*60);
        sender.sendMessage("Border center time changed to " + newBorderCenterTime + " minutes.");
    }

    @CommandPermission("uhc.config.border")
    @Subcommand("bordercenter")
    @CommandAlias("bordercenter")
    public void changeBorderCenter(CommandSender sender, Integer newBorderCenter) {
        instance.getGame().setBorderCenterTime(newBorderCenter);
        sender.sendMessage("Border center changed to " + newBorderCenter + " minutes.");
    }

    @CommandPermission("uhc.config.border")
    @Subcommand("bordersize")
    @CommandAlias("bordersize")
    public void changeBorderSize(CommandSender sender, Integer newBorderSize) {
        instance.getGame().setBorderSize(newBorderSize);
        sender.sendMessage("Border size changed " + newBorderSize + " blocks");
        Bukkit.getWorlds().forEach(it -> it.getWorldBorder().setSize(newBorderSize));
    }

    @CommandPermission("uhc.config.strength")
    @Subcommand("strength nerf")
    @CommandAlias("strength-nerf")
    public void changeStrengthNerf(CommandSender sender, @Optional Boolean bool) {
        if(bool == null)
            bool = !instance.getGame().isStrengthNerf();
                
        instance.getGame().setStrengthNerf(bool);
        sender.sendMessage("Strength has been set to: " + bool);
    }

    @CommandPermission("uhc.config.critical")
    @Subcommand("critical nerf")
    @CommandAlias("critical-nerf")
    public void changeCriticalNerf(CommandSender sender, @Optional Boolean bool) {
        if(bool == null)
            bool = !instance.getGame().isCriticalNerf();
                
        instance.getGame().setCriticalNerf(bool);
        sender.sendMessage("Critical nerf has been set to: " + bool);
    }

    @CommandPermission("uhc.config.applerate")
    @Subcommand("applerate")
    @CommandAlias("apple-rate")
    public void changeApplerate(CommandSender sender, Double rate) {
        sender.sendMessage("Applerate has been changed from " + instance.getGame().getApplerate() + "% to " + rate + "%");
        instance.getGame().setApplerate(rate);
    }

    @CommandPermission("uhc.config.slots")
    @Subcommand("setslots")
    @CommandAlias("slots||setslots||maxslots")
    public void changeSlots(CommandSender sender, Integer newSlots) {
        instance.getGame().setUhcslots(newSlots);
        sender.sendMessage("Slots set to: " + newSlots);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (Bukkit.getOnlinePlayers().size() >= instance.getGame().getUhcslots() && !e.getPlayer().hasPermission("reserved.slot"))
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&',
                    "&fServer is full! \n Get a reserved slot on &anoobstersuhc.buycraft.net"));
    }
    

}