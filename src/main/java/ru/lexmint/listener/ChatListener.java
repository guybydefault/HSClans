package ru.lexmint.listener;

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
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String format = event.getFormat();
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(event.getPlayer().getName(), true);

        String clanInfo = HSClans.instance.getLangConfig().getString("chat.clan-info");
        clanInfo = clanInfo.replaceFirst("%clan_role_tag%", cpLayer.getClanRole().getTag());
        clanInfo = clanInfo.replaceFirst("%clan%", cpLayer.getClan().getName());
        clanInfo = clanInfo.replaceFirst("%player_level%", cpLayer.getLevel().getName());
        clanInfo = HSClans.instance.getMessenger().translateColorCodes(clanInfo);
        format = format.replace("{CLAN_INFO}", clanInfo);

        event.setFormat(format);
    }
}
