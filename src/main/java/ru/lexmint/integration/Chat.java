package ru.lexmint.integration;

import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.IEssentialsChatListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import ru.lexmint.HSClans;
import ru.lexmint.listener.ChatListener;

/**
 * Deals with old versioned essentials chat.
 */
public class Chat {
    private static EssentialsChat essentialsChat;

    public static void setup() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("EssentialsChat");
        if (plugin == null) {
            HSClans.instance.getLogger().severe("EssentialsChat has not been found! Integration is unavailable.");
            return;
        }

        essentialsChat = (EssentialsChat) plugin;
        essentialsChat.addEssentialsChatListener(HSClans.instance.getDescription().getName(), new IEssentialsChatListener() {
            @Override
            public boolean shouldHandleThisChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
                return false;
            }

            @Override
            public String modifyMessage(AsyncPlayerChatEvent asyncPlayerChatEvent, Player player, String msg) {
                return msg.replace("[CLAN_INFO]", ChatListener.getClanInfo(asyncPlayerChatEvent.getPlayer()));
            }
        });
    }
}


