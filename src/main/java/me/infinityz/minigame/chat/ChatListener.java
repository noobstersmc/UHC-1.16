package me.infinityz.minigame.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;

@RequiredArgsConstructor
public class ChatListener implements Listener {
    private @NonNull UHC instance;
    private String format = "&7{prefix}{name}{suffix}:&f {message}";

    /*
     * Chat Channels
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void chatChannel(AsyncPlayerChatEvent e) {
        var player = e.getPlayer();
        var msg = e.getMessage();

        // Edge cases start
        if (msg.startsWith("!")) {
            e.setMessage(msg.replaceFirst("!", ""));
            if (player.getGameMode() == GameMode.SPECTATOR && !player.hasPermission("uhc.chat.spec")) {
                sendSpecMessage(player, msg);
                e.setCancelled(true);
            }
            return;
        } else if (msg.startsWith("@") && player.hasPermission("uhc.chat.spec")) {
            e.setMessage(msg.replaceFirst("@", ""));
            sendSpecMessage(player, msg);
            e.setCancelled(true);
            return;
        } else if (msg.startsWith("#") && player.hasPermission("uhc.chat.staff")) {
            e.setMessage(msg.replaceFirst("#", ""));
            sendStaffChat(player, msg);
            e.setCancelled(true);
            return;
        }
        // Edge cases end

        var chatManager = instance.getChatManager();
        var defaultChannel = chatManager.getDefaultOrNull(player);

        // Redirect base on defaultChannel
        if (defaultChannel != null && defaultChannel.equalsIgnoreCase("team")) {
            e.setCancelled(true);
            if (instance.getTeamManger().isTeams()) {
                var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                if (team != null) {
                    team.sendTeamMessageWithPrefix(" " + player.getName() + ": " + ChatColor.WHITE + e.getMessage());
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have a team, changing your chat to global");
                    chatManager.getDefaultChat().put(player.getUniqueId(), null);
                }
            } else {
                player.sendMessage(ChatColor.RED + "Teams are not enabled, switching you chat to global.");
                chatManager.getDefaultChat().put(player.getUniqueId(), null);
            }
            return;
        } else if (defaultChannel != null && defaultChannel.equals("staff")) {
            if (player.hasPermission("uhc.chat.staff")) {
                sendStaffChat(player, msg);
            } else {
                player.sendMessage(
                        ChatColor.RED + "You shouldn't be able to use staff chat, changing back to default.");
                chatManager.getDefaultChat().put(player.getUniqueId(), null);
            }
            e.setCancelled(true);
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR && !player.hasPermission("uhc.chat.spec")) {
            sendSpecMessage(player, msg);
            e.setCancelled(true);
            return;
        }
    }

    void sendSpecMessage(Player sender, String message) {
        var msg = ChatColor.GRAY + "[SPEC] " + colorize(
                replacedFormat(format.replace("{name}", sender.getName()).replace("{message}", message), sender));
        var win = instance.getGame().isHasSomeoneWon();
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (win || (all.getGameMode() == GameMode.SPECTATOR || all.hasPermission("uhc.chat.spec"))) {
                all.sendMessage(msg);
            }
        });
        Bukkit.getLogger().info(msg);

    }

    void sendStaffChat(Player sender, String message) {
        var msg = ChatColor.BLUE + "[STAFF] " + colorize(
                replacedFormat(format.replace("{name}", sender.getName()).replace("{message}", message), sender));
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (all.getGameMode() == GameMode.SPECTATOR || all.hasPermission("uhc.chat.spec")) {
                all.sendMessage(msg);
            }
        });
        Bukkit.getLogger().info(msg);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void checkForMute(AsyncPlayerChatEvent e) {
        if (instance.getGame().isGlobalMute() && !e.getPlayer().hasPermission("uhc.chat.global")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Globalmute is Enabled.");
        }
    }

    /*
     * Vault Handlers
     */
    @EventHandler
    public void onServiceChange(ServiceRegisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            instance.getChatManager().refreshVault();
        }
    }

    @EventHandler
    public void onServiceChange(ServiceUnregisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            instance.getChatManager().refreshVault();
        }
    }

    /*
     * Chat formatters.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void setFormat(AsyncPlayerChatEvent e) {
        e.setFormat(getFormatted());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatHigh(AsyncPlayerChatEvent e) {
        String format = e.getFormat();
        var vaultChat = instance.getChatManager().getVaultChat();
        if (vaultChat != null && format.contains("{prefix}")) {
            format = format.replace("{prefix}", vaultChat.getPlayerPrefix(e.getPlayer()));
        }
        if (vaultChat != null && format.contains("{suffix}")) {
            format = format.replace("{suffix}", vaultChat.getPlayerSuffix(e.getPlayer()));
        }
        format = format.replace("{name}", e.getPlayer().getName());

        e.setFormat(colorize(format));
    }

    private String replacedFormat(String format, Player p) {
        var vaultChat = instance.getChatManager().getVaultChat();
        if (vaultChat != null && format.contains("{prefix}")) {
            format = format.replace("{prefix}", vaultChat.getPlayerPrefix(p));
        }
        if (vaultChat != null && format.contains("{suffix}")) {
            format = format.replace("{suffix}", vaultChat.getPlayerSuffix(p));
        }
        return format.replace("{name}", p.getName());
    }

    private String getFormatted() {
        return format.replace("{message}", "%2$s").replace("{name}", "%1$s");
    }

    private String colorize(String message) {
        return translateHexColorCodes("&#", "", message);
    }

    private String translateHexColorCodes(String startTag, String endTag, String message) {
        final var COLOR_CHAR = '\u00A7';
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer,
                    COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR
                            + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR
                            + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
}