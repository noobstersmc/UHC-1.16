package me.infinityz.minigame.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
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

    @HelpCommand
    public void command(Player sender) {
        /* SCENARIOS ENABLED GUI*/
        instance.getGuiManager().getEnabledScenariosGui().open(sender);
    }

    @CommandPermission("uhc.scenarios")
    @Default
    @CommandCompletion("@scenarios")
    public void onEnable(CommandSender sender, String scenario) {
        var gamemode = getScenarioFromName(scenario);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        if(gamemode != null){
            if(gamemode.enableScenario()){
                Bukkit.broadcastMessage(senderName + ChatColor.GREEN + "Scenario " + gamemode.getName() + " has been enabled.");
            }else if(gamemode.disableScenario()){
                Bukkit.broadcastMessage(senderName + ChatColor.GREEN + "Scenario " + gamemode.getName() + " has been disabled.");
            }else{
                sender.sendMessage(ChatColor.RED + "Couldn't enable or disable " + gamemode.getName() + ".");
            }
        }else{
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