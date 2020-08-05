package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.tasks.ScatterTask;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("latescatter|ls|play")
public class LatescatterCMD extends BaseCommand {

    UHC instance;

    public LatescatterCMD(UHC instance) {
        this.instance = instance;
    }

    @Default
    @Conditions("ingame|time:max=1200")
    public void lateScatter(@Conditions("hasNotDied|spec") UHCPlayer uhcPlayer) {
        var player = Bukkit.getPlayer(uhcPlayer.getUUID());

        uhcPlayer.setAlive(true);

        player.sendMessage((ChatColor.of("#7ab83c") + "You have been scattered into the world."));

        player.teleport(ScatterTask.findScatterLocation(Bukkit.getWorlds().get(0),
                (int) Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2));
        player.setGameMode(GameMode.SURVIVAL);
    }

}