package me.infinityz.minigame.gamemodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.types.Cutclean;
import me.infinityz.minigame.gamemodes.types.EnderRespawn;
import me.infinityz.minigame.gamemodes.types.GoToHell;
import me.infinityz.minigame.gamemodes.types.Moles;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class GamemodeManager {
    UHC instance;
    private @Getter THashSet<IGamemode> gamemodesList = new THashSet<>();

    public GamemodeManager(UHC instance) {
        this.instance = instance;
        registerGamemode(new Cutclean(instance));
        registerGamemode(new EnderRespawn(instance));
        registerGamemode(new Moles(instance));
        registerGamemode(new GoToHell(instance));

        
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("scenarios", c -> {
            return gamemodesList.stream().map(IGamemode::getName).collect(Collectors.toList());
        });

    }

    public void registerGamemode(IGamemode gamemode) {
        gamemodesList.add(gamemode);
    }

    public Collection<IGamemode> getEnabledGamemodes() {
        final List<IGamemode> list = new ArrayList<>();
        gamemodesList.forEach(gamemode -> {
            if (gamemode.isEnabled())
                list.add(gamemode);
        });

        return list;
    }

    public  <T extends IGamemode> boolean isScenarioEnable(Class<T> clazz){
        for(var gamemode: gamemodesList){
            if(gamemode.isEnabled() && gamemode.getClass() == clazz){
                return true;
            }
            
        }
        return false;
    }

    //

    public String getEnabledGamemodesToString() {
        final StringBuilder sb = new StringBuilder();
        gamemodesList.forEach(all -> {
            if (all.isEnabled())
                sb.append(all.getName() + ", ");
        });
        return (sb.length() > 1 ? sb.toString().substring(0, sb.length() - 2) : "Vanilla") + ".";
    }

    public BaseComponent[] getScenariosWithDescription(){
        var componentBuilder = new ComponentBuilder();
        var enabledScenarios = getEnabledGamemodes();
        var iter =  enabledScenarios.iterator();

        if(iter.hasNext()){
            while(iter.hasNext()){
                var scenario = iter.next();        
                componentBuilder.append(scenario.getName() + (iter.hasNext() ? ", " : "."))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(scenario.getDescription())));
            }

        }else{
            componentBuilder.append("Vanilla")
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Vanilla game with some modifications.")));
        }

        
        
        return componentBuilder.create();
    }

}