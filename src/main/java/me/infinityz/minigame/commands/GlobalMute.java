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
@CommandPermission("globalmute.cmd")
@CommandAlias("globalmute")
public class GlobalMute extends BaseCommand {
    private @NonNull UHC instance;

    @CommandCompletion("@bool")
    @Default
    public void onCommand(CommandSender sender, @Optional Boolean bool) {
        if (bool != null) {
            instance.getGame().setGlobalMute(bool);
        } else {
            instance.getGame().setGlobalMute(!instance.getGame().isGlobalMute());
        }

        Bukkit.broadcastMessage(ChatColor.of("#2be49c")
                + (instance.getGame().isGlobalMute() ? "Globalmute Enabled." : "Globalmute Disabled."));

    }

}
