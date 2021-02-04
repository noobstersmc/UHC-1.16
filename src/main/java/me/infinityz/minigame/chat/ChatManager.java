package me.infinityz.minigame.chat;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;

public class ChatManager implements Listener {
    private UHC instance;
    private @Getter THashMap<UUID, String> defaultChat = new THashMap<>();
    private @Getter Chat vaultChat;

    public ChatManager(UHC instance) {
        this.instance = instance;
        refreshVault();

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("channels", c -> {
            var completion = new ArrayList<String>();
            completion.add("global");
            if (instance.getTeamManger().isTeams()) {
                completion.add("team");
            }
            return completion;

        });

        this.instance.getCommandManager().registerCommand(new ChatCommand());
        instance.getListenerManager().registerListener(this);

    }

    public String getDefaultOrNull(Player player) {
        return defaultChat.getOrDefault(player.getUniqueId(), "global");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void chatChannel(AsyncPlayerChatEvent e) {
        var player = e.getPlayer();

        var msg = e.getMessage();
        if (msg.startsWith("!")) {
            e.setMessage(msg.replaceFirst("!", ""));
            return;
        }else if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        var chatManager = instance.getChatManager();
        var defaultChannel = chatManager.getDefaultOrNull(player);

        // Redirect base on defaultChannel
        if (defaultChannel != null && defaultChannel.equalsIgnoreCase("team")) {
            e.setCancelled(true);
            if (instance.getTeamManger().isTeams()) {
                var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                if (team != null) {
                    team.sendTeamMessageWithPrefix(" " + player.getName() + ": " + ChatColor.GRAY + e.getMessage());
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have a team, switching to global chat.");
                    chatManager.getDefaultChat().put(player.getUniqueId(), null);
                }
            } else {
                player.sendMessage(ChatColor.RED + "Teams are not enabled, switching to global chat.");
                chatManager.getDefaultChat().put(player.getUniqueId(), null);
            }
            return;
        }

    }

    @RequiredArgsConstructor
    @CommandAlias("chat")
    public class ChatCommand extends BaseCommand {

        @CommandCompletion("@channels")
        @Default
        public void toggleChannel(final Player sender, String chat) {
            if (chat.equalsIgnoreCase("global") || chat.equalsIgnoreCase("gc")) {
                changeDefaultChat(sender, "global");
            } else if (chat.equalsIgnoreCase("team") || chat.equalsIgnoreCase("teams")) {
                if (instance.getTeamManger().isTeams())
                    changeDefaultChat(sender, "team");
                else
                    sender.sendMessage(
                            ChatColor.RED + "Channel " + ChatColor.WHITE + chat + ChatColor.RED + " is not enabled.");
            } else {
                sender.sendMessage(
                        ChatColor.RED + "Channel " + ChatColor.WHITE + chat + ChatColor.RED + " doesn't exist.");
            }
        }

        private void changeDefaultChat(final Player player, final String newChat) {
            instance.getChatManager().getDefaultChat().put(player.getUniqueId(), newChat.toLowerCase());
            player.sendMessage(ChatColor.GREEN + "Changing default chat to: " + ChatColor.WHITE + newChat.toLowerCase());

        }
    }

    public void refreshVault() {
        Chat vaultChat = instance.getServer().getServicesManager().load(Chat.class);
        if (vaultChat != this.vaultChat) {
            instance.getLogger().info(
                    "New Vault Chat implementation registered: " + (vaultChat == null ? "null" : vaultChat.getName()));
        }
        this.vaultChat = vaultChat;
    }

}