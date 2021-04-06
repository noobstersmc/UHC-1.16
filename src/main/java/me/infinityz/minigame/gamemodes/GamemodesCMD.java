package me.infinityz.minigame.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

/**
 * GamemodesCMD
 */
@CommandAlias("scenario|scenarios")
@RequiredArgsConstructor
public class GamemodesCMD extends BaseCommand {
    private @NonNull UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    @Default
    public void scenarios(CommandSender sender) {
        /* SCENARIOS ENABLED GUI */
        instance.getGuiManager().getMainGui().getEnabledScenariosGui().open((Player) sender);
    }

    @Subcommand("all")
    public void allList(Player sender) {
        instance.getGuiManager().getMainGui().getScenarioPages().get(0).open(sender);
    }


    @Subcommand("toggle")
    @CommandPermission("uhc.scenarios")
    @CommandCompletion("@scenarios")
    public void onEnable(CommandSender sender, String scenario) {
        var gamemode = getScenarioFromName(scenario);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        if (gamemode != null) {
            if (!gamemode.isEnabled()) {
                if(!gamemode.callEnable()){
                    sender.sendMessage(ChatColor.RED + "Couldn't enable " + gamemode.getName() + ".");
                }else{
                    Bukkit.broadcast(
                        senderName + ChatColor.YELLOW + "Scenario " + gamemode.getName() + " has been enabled.", permissionDebug);
                }

            } else {
                if(!gamemode.callDisable()){
                    sender.sendMessage(ChatColor.RED + "Couldn't disable " + gamemode.getName() + ".");
                }else{
                    Bukkit.broadcast(
                        senderName + ChatColor.YELLOW + "Scenario " + gamemode.getName() + " has been disabled.", permissionDebug);
                }

            }
        }else if(scenario != null){
            sender.sendMessage(ChatColor.RED + "Scenario " + scenario + " doesn't exist");
        }
    }

    private IGamemode getScenarioFromName(String name) {
        for (var scenario : instance.getGamemodeManager().getGamemodesList())
            if (scenario.getName().equalsIgnoreCase(name))
                return scenario;
        return null;
    }

}