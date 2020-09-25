package me.infinityz.minigame.chat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;

@RequiredArgsConstructor
public class ChatListener implements Listener {
    private @NonNull UHC instance;

    @EventHandler()
    public void onChat(AsyncPlayerChatEvent e) {
        var player = e.getPlayer();
        var channel = getPlayerChatChannel(player);
        var message = e.getMessage();
        if (channel == EnumChatChannel.GLOBAL)
            return;
        if(message.startsWith("!")){
            //Global chat message
            e.setMessage(message.replaceFirst("!", ""));
            return;
        }
        if(player.getGameMode() == GameMode.SPECTATOR && !player.hasPermission("uhc.chat.spec")){
            channel = EnumChatChannel.SPEC;
        }else if(player.hasPermission("uhc.chat.spec") && message.startsWith("@")){
            channel = EnumChatChannel.SPEC;
            message = message.replace("@", "");
        }
        sendMessageToChannel(player, e, channel);
    }
    private void sendMessageToChannel(Player sender, AsyncPlayerChatEvent event, EnumChatChannel channel) {
        Bukkit.broadcastMessage("Sending message to " + channel);
        if(channel == EnumChatChannel.TEAM){
            event.setCancelled(true);
            var team = instance.getTeamManger().getPlayerTeam(sender.getUniqueId());
            if(team != null){
                team.sendTeamMessageWithPrefix(" " + sender.getName() + ": " + ChatColor.GRAY + event.getMessage());
                team.getPlayerStream().filter(player -> player != sender).forEach(members -> {
                    members.playSound(members.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.25f, 1);
                });
            }else{
                sender.sendMessage("You don't have a team.");
            }
        }else if(channel == EnumChatChannel.SPEC){
            event.setCancelled(true);
            Bukkit.broadcastMessage("spec");
            System.out.println("Chat message");
        }else if(channel == EnumChatChannel.STAFF){
            event.setCancelled(true);
            Bukkit.broadcast(ChatColor.BOLD + "" + ChatColor.BLUE + "[STAFF] "+  ChatColor.RESET + event.getPlayer().getName() +": " + ChatColor.WHITE + event.getMessage(), "uhc.chat.staff");
        }

    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void checkForMute(AsyncPlayerChatEvent e) {
        if (instance.getGame().isGlobalMute() && !e.getPlayer().hasPermission("uhc.chat.global")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Globalmute is Enabled.");
        }
    }

    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpecChat(AsyncPlayerChatEvent e) {
        var player = e.getPlayer();
        var teamManager = instance.getTeamManger();
        if (teamManager.isTeams() && teamManager.isBroacastColor()) {
            var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
            if (team != null && team.isCustomName()) {
                var color = org.bukkit.ChatColor.values()[team.getTeamColorIndex()];
                e.setFormat(color + "[" + team.getTeamDisplayName() + "] " + ChatColor.RESET + e.getFormat());
            }
        }
    }


    private EnumChatChannel getPlayerChatChannel(Player player) {
        return getPlayerChatChannel(player.getUniqueId());
    }

    private EnumChatChannel getPlayerChatChannel(UUID uuid) {
        return instance.getChatManager().getCurrentPlayerChannel(uuid);
    }

}