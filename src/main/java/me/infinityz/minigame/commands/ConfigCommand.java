package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import me.infinityz.minigame.game.Game;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

/**
 * CONFIG COMMAND
 */
@CommandAlias("config")
public @RequiredArgsConstructor class ConfigCommand extends BaseCommand {

    private @NonNull UHC instance;

    //@CommandPermission("admin.perm")

    @CommandPermission("host.perm")
    @Subcommand("pvptime")
    @CommandAlias("pvptime")
    public void ChangePvpTime(CommandSender sender, Interger newPvpTime) {
        instance.getGame().setpvpTime(newPvpTime);
    }
}