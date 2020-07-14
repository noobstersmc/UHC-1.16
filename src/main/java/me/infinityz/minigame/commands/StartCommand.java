package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.scoreboard.ScatterScoreboard;

@CommandPermission("uhc.admin")
@CommandAlias("start")
public class StartCommand extends BaseCommand {
    UHC instance;

    public StartCommand(UHC instance) {
        this.instance = instance;
    }

    @Default
    public void scatter() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "locations scatter world");
        instance.getListenerManager().unregisterListener(instance.getListenerManager().getLobby());
        
        instance.getScoreboardManager().purgeScoreboards();

        Bukkit.getOnlinePlayers().forEach(players -> {
            //Efectos
            players.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 5));
            players.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 20, 5));
            players.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING , 20 * 20, 5));
            ScatterScoreboard sb = new ScatterScoreboard(players);
            sb.update();
            instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);
        });

        instance.getListenerManager().registerListener(instance.getListenerManager().getScatter());

    }

}