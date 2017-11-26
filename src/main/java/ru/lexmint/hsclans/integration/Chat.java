package ru.lexmint.hsclans.integration;

import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.IEssentialsChatListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.listener.ChatListener;

/**
 * Deals with old versioned (1.5.2 and before) essentials chat.
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


