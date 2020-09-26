package me.infinityz.minigame.chat;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import net.milkbowl.vault.chat.Chat;

public class ChatManager {
    private UHC instance;
    private @Getter THashMap<UUID, String> defaultChat = new THashMap<>();
    private @Getter Chat vaultChat;
    public ChatManager(UHC instance) {
        this.instance = instance;
        refreshVault();

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("channels", c->{
            var completion = new ArrayList<String>();
            completion.add("global");
            if(c.getSender().hasPermission("uhc.chat.staff")){
                completion.add("staff");
            }
            if(instance.getTeamManger().isTeams()){
                completion.add("team");
            }
            return completion;

        });

        instance.getCommandManager().registerCommand(new ChatCommand(instance));
        instance.getListenerManager().registerListener(new ChatListener(instance));

    }

    public String getDefaultOrNull(Player player){
        return defaultChat.getOrDefault(player.getUniqueId(), "global");
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