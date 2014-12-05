package ru.lexmint.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
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
        CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);
        // Make sure player has actual value of his power before logout (update power), save it.
        cpLayer.getPower();
        clanManager.updatePlayer(cpLayer);
        // Remove player from cache to save space in memory.
        clanManager.clearPlayerCache(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(event.getEntity().getName(), true);
        // Updating power.
        cpLayer.onDeath();
        HSClans.instance.getClanManager().updatePlayer(cpLayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        CPLayer cpLayer = HSClans.instance.getClanManager().getPlayer(event.getPlayer().getName(), true);
        // Make sure player has not got any power while he was dead.
        cpLayer.getPower();
        HSClans.instance.getClanManager().updatePlayer(cpLayer);
    }

}
