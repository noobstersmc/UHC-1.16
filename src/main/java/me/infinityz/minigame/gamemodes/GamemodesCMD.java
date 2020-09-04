package me.infinityz.minigame.gamemodes;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.types.EnderRespawnListener;

/**
 * GamemodesCMD
 */
@CommandAlias("scenario|scenarios")
@RequiredArgsConstructor
public class GamemodesCMD extends BaseCommand{
    private @NonNull UHC instance;

    @Default
    public void scenariosGui(Player sender){
        instance.getGamemodeManager().getGamemodesList().forEach(scen->{
            if(scen.getListener().isPresent()){
                var s = scen.getListener().get();
                if(s instanceof EnderRespawnListener){
                    EnderRespawnListener en = (EnderRespawnListener) s;
                    en.respawnAnimation(sender.getLocation());
                }
            }
        });


    }

    
}