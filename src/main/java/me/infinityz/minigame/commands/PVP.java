package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

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

@RequiredArgsConstructor
@CommandPermission("pvp.toggle")
@CommandAlias("pvp")
public class PVP extends BaseCommand {
    private @NonNull UHC instance;

    @CommandCompletion("@bool")
    @Default
    public void onCommand(CommandSender sender, @Optional Boolean bool) {
        if (bool != null) {
            instance.getGame().setPvp(bool);
        } else {
            instance.getGame().setPvp(!instance.getGame().isPvp());
        }

        Bukkit.broadcastMessage(ChatColor.YELLOW + "PvP has been set to " + instance.getGame().isPvp());
    }

}