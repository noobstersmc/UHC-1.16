package me.infinityz.minigame.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.Stage;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;

@RequiredArgsConstructor
@CommandAlias("helpop")
public class HelpopCommand extends BaseCommand {

    private @NonNull UHC instance;
    private final ChatColor sunflowerYellow = ChatColor.of("#DABC12");
    private Random random = new Random();

    @Default
    @Syntax("<message> &e- The message you want ")
    public void onList(Player player, String message) {
        if (message.length() < 1) {
            player.sendMessage("Correct usage: /helpop <message>");
            return;
        }

        player.sendMessage(sunflowerYellow + "Your message has been sent!");

        Bukkit.broadcast(sunflowerYellow + "[Helpop] " + ChatColor.GRAY + player.getName() + ": " + message,
                "helpop.hear");
    }

    @Subcommand("thanks|tk|tks")
    @CommandAlias("thanks|tk|tks")
    public void ty4Hosting(Player sender) {
        var game = instance.getGame();
        if(game.getGameStage() != Stage.INGAME){
            sender.sendMessage(ChatColor.RED + "Game hasn't started.");
            return;
        }
        var player = instance.getPlayerManager().getPlayer(sender.getUniqueId());
        if(player.isThanksHost()){
            sender.sendMessage(ChatColor.RED + "You already thanked the host.");
        }else if(Kern.getInstance().getChatManager().isGlobalmute()){
            sender.sendMessage(ChatColor.RED + "Globalmute is Enabled.");
        }else if(game.isHasSomeoneWon()){
            player.setThanksHost(true);
            Bukkit.broadcastMessage(thanks(ChatColor.AQUA + sender.getName()));
        }else if(player.isDead()){
            player.setThanksHost(true);
            Bukkit.getOnlinePlayers().forEach(p->{
                if(p.getGameMode() !=  GameMode.SURVIVAL)
                    p.sendMessage(thanks(ChatColor.AQUA + sender.getName()));
                
            });
        }else if(game.getGameTime() < game.getPvpTime()){
            player.setThanksHost(true);
            Bukkit.broadcastMessage(thanks(ChatColor.AQUA + sender.getName()));
        }else{
            sender.sendMessage(ChatColor.RED + "You can't thank the host at this moment, try again later.");
        }
    }

    public String thanks(String player){
        switch (random.nextInt(9)) {
            case 1: return player + ": Gracias por hostear papi";
            case 2: return player + ": Ty4Hosting!";
            case 3: return player + ": Thank you for the host!";
            case 4: return player + ": Gracias por el host pa";
            case 5: return player + ": Gracias por hostear!";
            case 6: return player + ": Thanks god for this UHC";
            case 7: return player + ": Merci pour l’host!";
            case 8: return player + ": Gracias por hostear mami";
    
            default: return player + ": Thanks for hosting!";
            
        }
    }

}