package ru.lexmint.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;

/**
 * Handles chat, replaces factions tags, etc.
 */
public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String format = event.getFormat();
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(event.getPlayer().getName(), true);

        StringBuilder clanInfo = new StringBuilder();
        clanInfo.append(cpLayer.getClanLeague().getTag());
        if (cpLayer.hasClan()) {
            clanInfo
                    .append(' ')
                    .append(cpLayer.getClanRole().getTag())
                    .append(cpLayer.getClan().getName());
        }
        String clanInfoString = ChatColor.translateAlternateColorCodes('&', clanInfo.toString());

        format = format.replace("{CLAN_INFO}", clanInfoString);

        event.setFormat(format);
    }
}
