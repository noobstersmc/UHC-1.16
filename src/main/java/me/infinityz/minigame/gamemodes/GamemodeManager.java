package me.infinityz.minigame.gamemodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.types.AdvancementHunter;
import me.infinityz.minigame.gamemodes.types.BackPack;
import me.infinityz.minigame.gamemodes.types.Baguettes;
import me.infinityz.minigame.gamemodes.types.BloodDiamonds;
import me.infinityz.minigame.gamemodes.types.BloodExperience;
import me.infinityz.minigame.gamemodes.types.BloodHunter;
import me.infinityz.minigame.gamemodes.types.BowLess;
import me.infinityz.minigame.gamemodes.types.ColdWeapons;
import me.infinityz.minigame.gamemodes.types.Cutclean;
import me.infinityz.minigame.gamemodes.types.DamageCycle;
import me.infinityz.minigame.gamemodes.types.DoubleGold;
import me.infinityz.minigame.gamemodes.types.DoubleLifeBar;
import me.infinityz.minigame.gamemodes.types.DoubleOres;
import me.infinityz.minigame.gamemodes.types.EnderRespawn;
import me.infinityz.minigame.gamemodes.types.EnderRespawnLeader;
import me.infinityz.minigame.gamemodes.types.FastLeaves;
import me.infinityz.minigame.gamemodes.types.FastSmelting;
import me.infinityz.minigame.gamemodes.types.FireLess;
import me.infinityz.minigame.gamemodes.types.FlorPoderosa;
import me.infinityz.minigame.gamemodes.types.GoToHell;
import me.infinityz.minigame.gamemodes.types.GoldenRetreiver;
import me.infinityz.minigame.gamemodes.types.GoneFishing;
import me.infinityz.minigame.gamemodes.types.HasteyBabies;
import me.infinityz.minigame.gamemodes.types.HasteyBoys;
import me.infinityz.minigame.gamemodes.types.HasteyBoysPlus;
import me.infinityz.minigame.gamemodes.types.HeavyPockets;
import me.infinityz.minigame.gamemodes.types.InfiniteEnchanter;
import me.infinityz.minigame.gamemodes.types.Limits;
import me.infinityz.minigame.gamemodes.types.LuckyLeaves;
import me.infinityz.minigame.gamemodes.types.MeetupDoubleLifeBar;
import me.infinityz.minigame.gamemodes.types.MetaGame;
import me.infinityz.minigame.gamemodes.types.Metrallesta;
import me.infinityz.minigame.gamemodes.types.Moles;
import me.infinityz.minigame.gamemodes.types.MonstersInc;
import me.infinityz.minigame.gamemodes.types.NineSlots;
import me.infinityz.minigame.gamemodes.types.NoFall;
import me.infinityz.minigame.gamemodes.types.NoLimpiar;
import me.infinityz.minigame.gamemodes.types.PermaGlow;
import me.infinityz.minigame.gamemodes.types.ShieldLess;
import me.infinityz.minigame.gamemodes.types.SkyHigh;
import me.infinityz.minigame.gamemodes.types.Switcheroo;
import me.infinityz.minigame.gamemodes.types.SwordLess;
import me.infinityz.minigame.gamemodes.types.ThunderKill;
import me.infinityz.minigame.gamemodes.types.TiempoBomba;
import me.infinityz.minigame.gamemodes.types.Timber;
import me.infinityz.minigame.gamemodes.types.TripleOres;
import me.infinityz.minigame.gamemodes.types.UHCGuest;
import me.infinityz.minigame.gamemodes.types.UHCLatam;
import me.infinityz.minigame.gamemodes.types.UHCMeetup;
import me.infinityz.minigame.gamemodes.types.UHCRun;
import me.infinityz.minigame.gamemodes.types.UHCVandalico;
import me.infinityz.minigame.gamemodes.types.UnDamaxe;
import me.infinityz.minigame.gamemodes.types.XPHunter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class GamemodeManager {
    UHC instance;
    private @Getter THashSet<IGamemode> gamemodesList = new THashSet<>();
    private @Getter @Setter int extraOreAmount = 0;
    private @Getter @Setter int extraGold = 0;

    public GamemodeManager(UHC instance) {
        this.instance = instance;
        // Scenarios
        registerGamemode(new Cutclean(instance));
        registerGamemode(new EnderRespawnLeader(instance));
        registerGamemode(new EnderRespawn(instance));
        registerGamemode(new Moles(instance));
        registerGamemode(new GoToHell(instance));
        registerGamemode(new BowLess(instance));
        registerGamemode(new UnDamaxe(instance));
        registerGamemode(new SwordLess(instance));
        registerGamemode(new ShieldLess(instance));
        registerGamemode(new FireLess(instance));
        registerGamemode(new NoFall(instance));
        registerGamemode(new HasteyBoys(instance));
        registerGamemode(new Limits(instance));
        registerGamemode(new LuckyLeaves(instance));
        registerGamemode(new Timber(instance));
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
        registerGamemode(new GoldenRetreiver(instance));
        registerGamemode(new FlorPoderosa(instance));
        registerGamemode(new TiempoBomba(instance));
        registerGamemode(new NoLimpiar(instance));
        registerGamemode(new FastLeaves());
        registerGamemode(new NineSlots(instance));
        registerGamemode(new DoubleGold(instance));
        registerGamemode(new ThunderKill(instance));
        registerGamemode(new XPHunter(instance));
        registerGamemode(new FastSmelting(instance));
        registerGamemode(new MonstersInc(instance));
        registerGamemode(new PermaGlow(instance));
        registerGamemode(new BloodExperience(instance));
        registerGamemode(new HasteyBoysPlus(instance));
        registerGamemode(new DoubleLifeBar(instance));
        registerGamemode(new MeetupDoubleLifeBar(instance));
        registerGamemode(new Metrallesta(instance));
        registerGamemode(new ColdWeapons(instance));
        registerGamemode(new HeavyPockets(instance));
        registerGamemode(new HasteyBabies(instance));
        registerGamemode(new DamageCycle(instance));

        //Invitados
        registerGamemode(new UHCVandalico(instance));
        registerGamemode(new UHCLatam(instance));
        registerGamemode(new Baguettes(instance));
        registerGamemode(new UHCGuest(instance));

        // Scenarios pack
        registerGamemode(new UHCRun(instance, this));
        registerGamemode(new MetaGame(instance));

        // mode
        registerGamemode(new UHCMeetup(instance));

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("scenarios",
                c -> gamemodesList.stream().map(IGamemode::getName).collect(Collectors.toList()));

    }

    public void registerGamemode(IGamemode gamemode) {
        gamemodesList.add(gamemode);
    }

    public Collection<IGamemode> getEnabledGamemodes() {
        final List<IGamemode> list = new ArrayList<>();
        // Remove all scenarios inside pack if pack is toggled.
        var uhcRun = getScenario(UHCRun.class);
        if (uhcRun.isEnabled()) {
            list.removeAll(uhcRun.getGamemodes());
        }

        var metaGame = getScenario(MetaGame.class);
        if (metaGame.isEnabled()) {
            list.removeAll(metaGame.getGamemodes());
        }

        gamemodesList.forEach(gamemode -> {
            if (gamemode.isEnabled())
                list.add(gamemode);
        });

        return list;
    }

    public <T extends IGamemode> T getScenario(Class<T> clazz) {
        for (var gamemode : gamemodesList)
            if (gamemode.getClass() == clazz)
                return clazz.cast(gamemode);

        return null;
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

        var uhcRun = getScenario(UHCRun.class);
        if (uhcRun.isEnabled()) {
            enabledScenarios.removeAll(uhcRun.getGamemodes());
        }

        var metaGame = getScenario(MetaGame.class);
        if (metaGame.isEnabled()) {
            enabledScenarios.removeAll(metaGame.getGamemodes());
        }

        var iter = enabledScenarios.iterator();

        if (iter.hasNext())
            while (iter.hasNext())
                sb.append(iter.next().getName() + (iter.hasNext() ? ", " : "."));
        else
            sb.append("Vanilla+");

        return sb.toString();
    }

    public String getFirstEnabledScenario() {
        for (var gm : gamemodesList) {
            if (gm.isEnabled() && gm.getClass() != UHCMeetup.class && gm.getClass() != NoLimpiar.class)
                return gm.getName();
        }
        return "Vanilla";
    }

    public BaseComponent[] getScenariosWithDescription() {
        var componentBuilder = new ComponentBuilder();
        var enabledScenarios = getEnabledGamemodes();

        var uhcRun = getScenario(UHCRun.class);
        if (uhcRun.isEnabled()) {
            enabledScenarios.removeAll(uhcRun.getGamemodes());
        }

        var metaGame = getScenario(MetaGame.class);
        if (metaGame.isEnabled()) {
            enabledScenarios.removeAll(metaGame.getGamemodes());
        }

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