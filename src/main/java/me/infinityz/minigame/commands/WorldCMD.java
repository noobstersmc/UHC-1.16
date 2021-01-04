package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("world")
@CommandPermission("admin.uhc")
public class WorldCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Default
    @CommandCompletion("@worlds")
    public void tpWorld(Player sender, World world) {
        sender.teleport(world.getSpawnLocation());
        sender.sendMessage("Teleported to world " + world);
    }

    @Subcommand("create")
    public void worldCreate(Player sender, String world) {
        WorldCreator newWorld = new WorldCreator(world);
        newWorld.environment(Environment.NORMAL);
        newWorld.createWorld();
        
        sender.sendMessage(ChatColor.GREEN + "World " + world + " created.");
    }

    @Subcommand("unload")
    @CommandCompletion("@worlds")
    public void worldRemove(Player sender, World world) {
        Bukkit.unloadWorld(world, false);
        sender.sendMessage(ChatColor.RED + "World " + world + " unloaded.");
    }

    @Subcommand("load nether")
    public void worldLoadNether(Player sender) {
        instance.getWorld_nether().environment(Environment.NETHER);
        instance.getWorld_nether().createWorld();
        sender.sendMessage(ChatColor.GREEN + "World nether loaded.");
    }

    @Subcommand("load end")
    public void worldLoadEnd(Player sender) {
        instance.getWorld_end().environment(Environment.THE_END);
        instance.getWorld_end().createWorld();
        sender.sendMessage(ChatColor.GREEN + "World end loaded.");
    }

    @Subcommand("load world")
    public void worldLoadWorld(Player sender) {
        instance.getWorld().createWorld();
        sender.sendMessage(ChatColor.GREEN + "World world loaded.");
    }


}