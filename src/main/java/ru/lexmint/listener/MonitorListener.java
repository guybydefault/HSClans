package ru.lexmint.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Claim;
import ru.lexmint.domain.ClanManager;

import java.util.HashMap;

/**
 * Listener, which makes sure that CPlayer objects created when player joins the server and removed when he
 * logs out.
 */
public class MonitorListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        /**
         * Checks if player has changed a chunk. If it is so - then we continue.
         */
        if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
            return;
        }

        Player player = event.getPlayer();
        ClanManager clanManager = HSClans.instance.getClanManager();

        Claim from = clanManager.getClaim(event.getFrom().getChunk().getX(), event.getFrom().getChunk().getZ());
        Claim to = clanManager.getClaim(event.getTo().getChunk().getX(), event.getTo().getChunk().getZ());

        /**
         * Checks if owner of the land is the same or not. If the same - returns.
         */
        if ((from == null && to == null) || (from != null && to != null && from.getClan() == to.getClan())) {
            return;
        }

        if (to != null) {
            HSClans.instance.getMessenger().message("land.clan", player, to.getClan().getClanLevel().getName(), to.getClan().getName(), to.getClan().getDescription());
        } else {
            HSClans.instance.getMessenger().message("land.wilderness", player);

        }
    }

    HashMap<String, Long> playTimes = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ClanManager clanManager = HSClans.instance.getClanManager();
        if (clanManager.getPlayer(player.getName(), true) == null) {
            clanManager.createPlayer(player.getName());
        }
        playTimes.put(event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onPlayerLeave(event.getPlayer());
    }

    public void onPlayerLeave(Player player) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);
        // Make sure player has actual value of his power before logout (update power), save it.
        cpLayer.getPower();

        // Deals with player's play time on server.
        if (playTimes.containsKey(player.getName())) {
            double hoursPlayed = (System.currentTimeMillis() - playTimes.remove(player.getName())) / 1000d / 60 / 60;
            cpLayer.alterHoursPlayed(hoursPlayed);
        }

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
        // Incrementing deaths
        cpLayer.getStats().incrementDeaths();
        clanManager.updatePlayer(cpLayer);

        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            CPLayer kPlayer = clanManager.getPlayer(killer.getName(), false);
            kPlayer.getStats().incrementKills();
            clanManager.updatePlayer(kPlayer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        CPLayer cpLayer = HSClans.instance.getClanManager().getPlayer(event.getPlayer().getName(), true);
        // Make sure player has not got any power while he was dead.
        cpLayer.getPower();
        HSClans.instance.getClanManager().updatePlayer(cpLayer);
    }
}
