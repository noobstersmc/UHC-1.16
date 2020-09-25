package me.infinityz.minigame.chat;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.types.Moles;

@RequiredArgsConstructor
@CommandAlias("chat")
public class ChatCommand extends BaseCommand {

    private @NonNull UHC instance;

    @Default
    public void toggleChannel(final Player sender) {
        var chatManager = instance.getChatManager();

        var currentChannel = chatManager.getCurrentPlayerChannel(sender);
        var availableChannels = getAvailableChannels(sender);

        var nextChannel = getNextChannel(currentChannel, availableChannels);

        sender.sendMessage("Moving from " + currentChannel + " to " + nextChannel);

        chatManager.setPlayerChatChannel(sender.getUniqueId(), nextChannel);
        
    }

    public EnumChatChannel getNextChannel(final EnumChatChannel current, final EnumChatChannel[] channels) {
        var currentIndex = findCurrentIndex(current, channels);
        var max = channels.length;
        if (++currentIndex > max - 1)
            return channels[0];

        return channels[currentIndex];
    }

    public EnumChatChannel[] getAvailableChannels(final Player player) {
        var enums = EnumChatChannel.values();

        if (!player.hasPermission("uhc.chat.staff")) {
            enums = (EnumChatChannel[]) ArrayUtils.removeElement(enums, EnumChatChannel.STAFF);
            enums = (EnumChatChannel[]) ArrayUtils.removeElement(enums, EnumChatChannel.SPEC);
        }
        if (!instance.getGamemodeManager().isScenarioEnable(Moles.class)) {
            enums = (EnumChatChannel[]) ArrayUtils.removeElement(enums, EnumChatChannel.MOLES);
        }

        return enums;
    }

    public int findCurrentIndex(final EnumChatChannel current, final EnumChatChannel[] channels) {
        int i = 0;
        for (; i < channels.length; i++)
            if (channels[i].equals(current))
                break;
        return i;
    }
}
