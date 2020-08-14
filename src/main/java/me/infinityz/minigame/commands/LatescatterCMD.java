package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("latescatter|ls|play")
public class LatescatterCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Default
    @Conditions("ingame|time:max=1200")
    public void lateScatter(@Conditions("hasNotDied|spec") UHCPlayer uhcPlayer) {
        var player = Bukkit.getPlayer(uhcPlayer.getUUID());

        uhcPlayer.setAlive(true);

        player.sendMessage((ChatColor.of("#7ab83c") + "You have been scattered into the world."));

        player.teleport(ChunksManager.findScatterLocation(Bukkit.getWorlds().get(0),
                (int) Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2));
        player.setGameMode(GameMode.SURVIVAL);
    }

}