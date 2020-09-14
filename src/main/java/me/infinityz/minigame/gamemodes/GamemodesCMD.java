package me.infinityz.minigame.gamemodes;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;

/**
 * GamemodesCMD
 */
@CommandAlias("scenario|scenarios")
@RequiredArgsConstructor
public class GamemodesCMD extends BaseCommand {
    private @NonNull UHC instance;

    @HelpCommand
    public void command(CommandSender sender) {
        sender.sendMessage("Scenarios coso");

    }

    @CommandPermission("uhc.gamemodes.toggle")
    @Default
    @CommandCompletion("@scenarios")
    public void onEnable(CommandSender sender, String scenario) {
        var gamemode = getScenarioFromName(scenario);
        if(gamemode != null){
            if(gamemode.enableScenario()){
                sender.sendMessage("Scenario " + gamemode.getName() + " has been enabled.");
            }else if(gamemode.disableScenario()){
                sender.sendMessage("Scenario" + gamemode.getName() + " has been disabled.");
            }else{
                sender.sendMessage("Couldn't enable or disable " + gamemode.getName() + ".");
            }
        }else{
            sender.sendMessage("Scenario " + scenario + " doesn't exist");
        }
    }

    private IGamemode getScenarioFromName(String name) {
        for (var scenario : instance.getGamemodeManager().getGamemodesList())
            if (scenario.getName().equalsIgnoreCase(name))
                return scenario;
        return null;
    }

}