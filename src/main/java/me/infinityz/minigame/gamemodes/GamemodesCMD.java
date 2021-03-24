package me.infinityz.minigame.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
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

    @Default
    @CommandPermission("uhc.scenarios")
    @CommandCompletion("@scenarios")
    public void onEnable(CommandSender sender, @Optional String scenario) {

        if (scenario == null && sender instanceof Player) {
            /* SCENARIOS ENABLED GUI */
            instance.getGuiManager().getEnabledScenariosGui().open((Player) sender);
        }

        var gamemode = getScenarioFromName(scenario);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        if (gamemode != null) {
            if (!gamemode.isEnabled()) {
                Bukkit.broadcastMessage(
                        senderName + ChatColor.GREEN + "Scenario " + gamemode.getName() + " has been enabled.");
                gamemode.callEnable();
            } else {
                Bukkit.broadcastMessage(
                        senderName + ChatColor.GREEN + "Scenario " + gamemode.getName() + " has been disabled.");
                gamemode.callDisable();
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