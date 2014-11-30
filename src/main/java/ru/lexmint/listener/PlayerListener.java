package ru.lexmint.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.lexmint.HSClans;
import ru.lexmint.domain.ClanManager;

/**
 * Listener, which makes sure that CPlayer objects created when player joins the server and removed when he
 * logs out.
 */
public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ClanManager clanManager = HSClans.instance.getClanManager();
        if (clanManager.getPlayer(player.getName(), true) == null) {
            clanManager.createPlayer(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ClanManager clanManager = HSClans.instance.getClanManager();
        clanManager.clearPlayerCache(player.getName());
    }

}
