package me.infinityz.minigame.chat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NonNull;
import me.infinityz.minigame.UHC;
import net.milkbowl.vault.chat.Chat;

public class ChatManager {
    private @NonNull Chat vaulChat;
    private @Getter HashMap<Long, EnumChatChannel> chatChannelMap = new HashMap<>();

    public ChatManager(UHC instance){
        instance.getCommandManager().registerCommand(new ChatCommand(instance));
        instance.getListenerManager().registerListener(new ChatListener(instance));

        /*
        * https://github.com/lucko/VaultChatFormatter/blob/master/src/main/java/me/lucko/chatformatter/ChatFormatterPlugin.java
        */

    }
    public EnumChatChannel getCurrentPlayerChannel(final Player player){
        return getCurrentPlayerChannel(player.getUniqueId());
    }
    public EnumChatChannel getCurrentPlayerChannel(final UUID uuid){
        return chatChannelMap.getOrDefault(uuid.getMostSignificantBits(), EnumChatChannel.GLOBAL);
    }

    public void setPlayerChatChannel(final UUID uuid, final EnumChatChannel channel){
        chatChannelMap.put(uuid.getMostSignificantBits(), channel);
    }
    
}