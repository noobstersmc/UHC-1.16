package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("world")
public class WorldCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Default
    @CommandPermission("world.cmd")
    @CommandCompletion("@worlds")
    public void tpWorld(Player sender, World world) {
        sender.teleport(world.getSpawnLocation());
        sender.sendMessage("Teleported to world " + world);
    }

    @CommandPermission("worldload.cmd")
    @Subcommand("worldload")
    @CommandAlias("worldload")
    public void wordLoad(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "chunky world world");
        Bukkit.dispatchCommand(sender, "chunky radius " + instance.getChunkManager().getBorder());
        Bukkit.dispatchCommand(sender, "chunky start");

        if (instance.getGame().isNether()) {
            worldCreateAndLoad(sender, "NETHER", "world_nether", Long.parseLong(UHC.getSEED()));
            Bukkit.dispatchCommand(sender, "chunky world world_nether");
            Bukkit.dispatchCommand(sender, "chunky radius " + instance.getChunkManager().getBorder());
            Bukkit.dispatchCommand(sender, "chunky start");
        }
        if (instance.getGame().isEnd()) {
            worldCreateAndLoad(sender, "END", "world_end", Long.parseLong(UHC.getSEED()));
            Bukkit.dispatchCommand(sender, "chunky world world_end");
            Bukkit.dispatchCommand(sender, "chunky radius " + instance.getChunkManager().getBorder());
            Bukkit.dispatchCommand(sender, "chunky start");
        }

    }

    @CommandPermission("worldstatus.cmd")
    @Subcommand("worldstatus")
    @CommandAlias("worldstatus")
    public void worldload(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "chunky-hynix status");
    }

    @Subcommand("unload")
    @CommandPermission("world.cmd")
    @CommandCompletion("@worlds")
    public void worldRemove(CommandSender sender, World world) {
        Bukkit.unloadWorld(world, false);
        sender.sendMessage(ChatColor.RED + "World " + world + " unloaded.");
    }

    @Subcommand("recycle")
    @CommandPermission("recycle.cmd")
    public void recycle(CommandSender sender) {
    }

    @Subcommand("load")
    @CommandPermission("world.cmd")
    public void worldCreateAndLoad(CommandSender sender, String type, String newWorld, @Optional Long seed) {
        var world = Bukkit.getWorld(newWorld);
        if (world != null) {
            sender.sendMessage(ChatColor.RED + "World " + newWorld + " is already created.");
            return;
        } 
        WorldCreator worldCreator = new WorldCreator(newWorld);

        if (seed == null) {
            worldCreator.seed(Long.parseLong(UHC.getSEED()));
        } else {
            worldCreator.seed(seed);
        }

        switch (type.toLowerCase()) {
            case "nether": {
                worldCreator.environment(Environment.NETHER);
                break;
            }
            case "end": {
                worldCreator.environment(Environment.THE_END);
                break;
            }
            case "normal": {
                worldCreator.environment(Environment.NORMAL);
                break;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "Unknown world type.");
                return;
            }
        }

        world = worldCreator.createWorld();
        sender.sendMessage(ChatColor.GREEN + "World " + newWorld + " created.");
        refreshWorldCMDs(world);
    }

    private void refreshWorldCMDs(World world) {
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, instance.getGame().isAdvancements());
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setSpawnLocation(0, world.getHighestBlockAt(0, 0).getZ() + 10, 0);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(instance.getGame().getBorderSize());
        world.getWorldBorder().setDamageBuffer(0.0);
        world.getWorldBorder().setDamageAmount(0.0);
    }

}