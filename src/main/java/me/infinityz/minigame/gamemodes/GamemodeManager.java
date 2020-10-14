package me.infinityz.minigame.gamemodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.types.UnDamaxe;
import me.infinityz.minigame.gamemodes.types.BowLess;
import me.infinityz.minigame.gamemodes.types.Cutclean;
import me.infinityz.minigame.gamemodes.types.DebugMode;
import me.infinityz.minigame.gamemodes.types.FireLess;
import me.infinityz.minigame.gamemodes.types.GoToHell;
import me.infinityz.minigame.gamemodes.types.Moles;
import me.infinityz.minigame.gamemodes.types.NoFall;
import me.infinityz.minigame.gamemodes.types.Baguettes;
import me.infinityz.minigame.gamemodes.types.ShieldLess;
import me.infinityz.minigame.gamemodes.types.SwordLess;
import me.infinityz.minigame.gamemodes.types.uhclatam.UHCLatam;
import me.infinityz.minigame.gamemodes.types.HasteyBoys;
import me.infinityz.minigame.gamemodes.types.Limits;
import me.infinityz.minigame.gamemodes.types.LuckyLeaves;
import me.infinityz.minigame.gamemodes.types.Timber;
import me.infinityz.minigame.gamemodes.types.Totems.Totems;
import me.infinityz.minigame.gamemodes.types.DoubleOres;
import me.infinityz.minigame.gamemodes.types.TripleOres;
import me.infinityz.minigame.gamemodes.types.SkyHigh;
import me.infinityz.minigame.gamemodes.types.BloodHunter;
import me.infinityz.minigame.gamemodes.types.BackPack;
import me.infinityz.minigame.gamemodes.types.Switcheroo;
import me.infinityz.minigame.gamemodes.types.InfiniteEnchanter;
import me.infinityz.minigame.gamemodes.types.GoneFishing;
import me.infinityz.minigame.gamemodes.types.BloodDiamonds;
import me.infinityz.minigame.gamemodes.types.AdvancementHunter;
import me.infinityz.minigame.gamemodes.types.erespawn.EnderRespawn;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class GamemodeManager {
    UHC instance;
    private @Getter THashSet<IGamemode> gamemodesList = new THashSet<>();
    private @Getter @Setter int extraOreAmount = 0;

    public GamemodeManager(UHC instance) {
        this.instance = instance;
        registerGamemode(new Cutclean(instance));
        registerGamemode(new EnderRespawn(instance));
        registerGamemode(new Moles(instance));
        registerGamemode(new GoToHell(instance));
        registerGamemode(new UHCLatam(instance));
        registerGamemode(new BowLess(instance));
        registerGamemode(new UnDamaxe(instance));
        registerGamemode(new SwordLess(instance));
        registerGamemode(new ShieldLess(instance));
        registerGamemode(new FireLess(instance));
        registerGamemode(new NoFall(instance));
        registerGamemode(new DebugMode(instance));
        registerGamemode(new Baguettes(instance));
        registerGamemode(new HasteyBoys(instance));
        registerGamemode(new Limits(instance));
        registerGamemode(new LuckyLeaves(instance));
        registerGamemode(new Timber(instance));
        registerGamemode(new Totems(instance));
        registerGamemode(new DoubleOres(instance));
        registerGamemode(new TripleOres(instance));
        registerGamemode(new BloodHunter(instance));
        registerGamemode(new SkyHigh(instance));
        registerGamemode(new BackPack(instance));
        registerGamemode(new Switcheroo(instance));
        registerGamemode(new InfiniteEnchanter(instance));
        registerGamemode(new GoneFishing(instance));
        registerGamemode(new BloodDiamonds(instance));
        registerGamemode(new AdvancementHunter(instance));

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("scenarios",
                c -> gamemodesList.stream().map(IGamemode::getName).collect(Collectors.toList()));

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

    public <T extends IGamemode> boolean isScenarioEnable(Class<T> clazz) {
        for (var gamemode : gamemodesList) {
            if (gamemode.isEnabled() && gamemode.getClass() == clazz) {
                return true;
            }

        }
        return false;
    }

    //

    public String getEnabledGamemodesToString() {
        var sb = new StringBuilder();
        var enabledScenarios = getEnabledGamemodes();
        var iter = enabledScenarios.iterator();

        if (iter.hasNext())
            while (iter.hasNext())
                sb.append(iter.next().getName() + (iter.hasNext() ? ", " : "."));
        else
            sb.append("Vanilla+");

        return sb.toString();
    }

    public BaseComponent[] getScenariosWithDescription() {
        var componentBuilder = new ComponentBuilder();
        var enabledScenarios = getEnabledGamemodes();
        var iter = enabledScenarios.iterator();
        componentBuilder.color(ChatColor.WHITE);

        if (iter.hasNext()) {
            while (iter.hasNext()) {
                var scenario = iter.next();
                componentBuilder.append(scenario.getName() + (iter.hasNext() ? ", " : "."))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(scenario.getDescription())));
            }

        } else {
            componentBuilder.append("Vanilla+").event(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Vanilla game with some modifications.")));
        }

        return componentBuilder.create();
    }

}