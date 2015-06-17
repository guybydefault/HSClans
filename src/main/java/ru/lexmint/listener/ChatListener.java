package ru.lexmint.listener;

import org.bukkit.entity.Player;
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
        event.setFormat(event.getFormat().replace("[CLAN_INFO]", getClanInfo(event.getPlayer())));
    }

    public static String getClanInfo(Player player) {
        String clanInfo;
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);

        if (cpLayer.hasClan()) {
            clanInfo = HSClans.instance.getLangConfig().getString("chat.clan-info.clan");
            clanInfo = clanInfo.replaceFirst("%clan%", cpLayer.getClan().getName());
        } else {
            clanInfo = HSClans.instance.getLangConfig().getString("chat.clan-info.no-clan");
        }
        clanInfo = clanInfo.replaceFirst("%clan_role_tag%", cpLayer.getClanRole().getTag());
        clanInfo = clanInfo.replaceFirst("%player_level%", cpLayer.getLevel().getName());
        clanInfo = clanInfo.replaceFirst("%rate%", String.valueOf(cpLayer.getHSRateView()));

        clanInfo = HSClans.instance.getMessenger().translateColorCodes(clanInfo);
        return clanInfo;
    }


}
