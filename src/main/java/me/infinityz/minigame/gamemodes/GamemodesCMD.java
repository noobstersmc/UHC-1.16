package me.infinityz.minigame.gamemodes;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
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
    public void onEnable(CommandSender sender, String scenario) {
        var optional = instance.getGamemodeManager().getGamemodesList().stream()
                .filter(scen -> scen.getName().equalsIgnoreCase(scenario)).findAny();

        if (optional.isPresent()) {
            if (optional.get().enableScenario(instance)) {
                sender.sendMessage("Scenario " + scenario + " has been enabled");
            }else if(optional.get().disableScenario(instance)){
                sender.sendMessage("Scenario " + scenario + " has been disabled");
            }else{
                sender.sendMessage("Couldn't do that");
            }
        } else {
            // Scenario not found
            command(sender);
        }
    }

}