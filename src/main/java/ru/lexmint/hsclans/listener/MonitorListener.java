package ru.lexmint.hsclans.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.cmd.AutoclaimCommand;
import ru.lexmint.hsclans.cmd.BypassCommand;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Claim;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.events.PowerLossDeathEvent;

import java.util.HashMap;

/**
 * Listener, which makes sure that CPlayer objects created when player joins the server and removed when he
 * logs out.
 */
public class MonitorListener implements Listener {

    public HashMap<String, Long> getPlayTimes() {
        return playTimes;
    }

    /**
     * Stores players' join times. It is used to count their play time when they leave from server.
     */
    private final HashMap<String, Long> playTimes = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        /**
         * Checks if player has changed a chunk. If it is so - then we continue.
         */
        if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
            return;
        }

        /** Autoclaim mode **/
        Player player = event.getPlayer();
        if (AutoclaimCommand.isAutoclaiming(player.getName())) {
            HSClans.instance.getCommandManager().performClaimCommand(player);
        }

        ClanManager clanManager = HSClans.instance.getClanManager();

        Claim from = clanManager.getClaim(event.getFrom().getChunk().getX(), event.getFrom().getChunk().getZ(), event.getFrom().getWorld());
        Claim to = clanManager.getClaim(event.getTo().getChunk().getX(), event.getTo().getChunk().getZ(), event.getTo().getWorld());


        if (to == null) {
            if (from == null) {
                // Wilderness -> Wilderness
                return;
            } else {
                // Clan -> Wilderness
                HSClans.instance.getMessenger().message("land.wilderness", player);
                return;
            }
        } else {
            CPLayer cPLayer = clanManager.getPlayer(player.getName(), false);

            if (cPLayer.getClan() == to.getClan()) {
                /**
                 *  Wilderness -> Own land
                 *  Own land with minRole A -> Own land with minRole B
                 *  Enemy land -> Own land
                 */
                if (from == null || from.getClan() != to.getClan() || from.getMinRole() != to.getMinRole()) {
                    HSClans.instance.getMessenger().message("land.clan.own", player, to.getClan().getLevel().getName(), to.getClan().getName(), to.getMinRole().getName(), to.getClan().getDescription());
                }
            } else {
                /**
                 * Wilderness -> Enemy land
                 * Enemy land of clan A -> Enemy land of clan B
                 */
                if (from == null || from.getClan() != to.getClan()) {
                    if (cPLayer.getClan() != null && cPLayer.getClan().isAlliedWith(to.getClan())) {
                        HSClans.instance.getMessenger().message("land.clan.other.ally", player, to.getClan().getLevel().getName(), to.getClan().getName(), to.getClan().getDescription());
                    } else {
                        HSClans.instance.getMessenger().message("land.clan.other.enemy", player, to.getClan().getLevel().getName(), to.getClan().getName(), to.getClan().getDescription());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onPlayerLeave(event.getPlayer());
    }

    public void onPlayerLeave(Player player) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        final CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);
        // Make sure player has actual value of his power before logout (update power), save it.
        cpLayer.getPower(true);

        /** Last player in clan leaves the server. */
        if (cpLayer.hasClan()) {
            if (cpLayer.getClan().getMembersOnline().size() == 1) {
                cpLayer.getClan().setLastPlayed(System.currentTimeMillis());
            }
        }

        updateHoursPlayed(cpLayer);

        cpLayer.setLastPlayed(System.currentTimeMillis());

        clanManager.updatePlayer(cpLayer);
        // Remove player from cache to save space in memory.
        if (HSClans.instance.getSettings().getBoolean("performance.cache-clear-on-leave")) {
            clanManager.clearPlayerCache(player.getName());
        }

        /**
         * Fix of exploit. When we kicked one bad moderator from his role, removed permissions, then, he joined the
         * server and with bypass mode he disbanded a lot of clans.
         */
        BypassCommand.removeBypass(player.getName());
    }

    /**
     * This method does not save new player's data to database! Make an update to storage by hand.
     *
     * @param cpLayer CPlayer object.
     */
    public void updateHoursPlayed(CPLayer cpLayer) {
        // Deals with player's play time on server.
        if (playTimes.containsKey(cpLayer.getName())) {
            double hoursPlayed = (System.currentTimeMillis() - playTimes.remove(cpLayer.getName())) / 1000d / 60 / 60;
            cpLayer.alterHoursPlayed(hoursPlayed);
        }
    }

    public void onPlayerJoin(Player player) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);
        if (cpLayer == null) {
            cpLayer = clanManager.createPlayer(player.getName());
        } else {
            cpLayer.losePowerFromBeingOffline();
            cpLayer.setLastPowerUpdateTime(System.currentTimeMillis());
            clanManager.updatePlayer(cpLayer);
        }

        /* TODO Tournament feature */
        if (HSClans.instance.getSettings().getBoolean("tournament.enable")) {
            if (!cpLayer.hasClan() && !player.hasPermission("hsclans.command.bypass")) {
                player.kickPlayer(HSClans.instance.getMessenger().format("messages.errors.tournament-join"));
            }
        }
        /* Tournament feature */

        playTimes.put(player.getName(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(event.getEntity().getName(), true);
        // Updating power (providing API for other plugins to cancel player power loss)
        PowerLossDeathEvent deathEvent = new PowerLossDeathEvent(cpLayer);
        Bukkit.getServer().getPluginManager().callEvent(deathEvent);
        if (!deathEvent.isCancelled()) {
            cpLayer.losePowerOnDeath();
        }
        // Incrementing deaths
        cpLayer.incrementDeaths();

        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            CPLayer kPlayer = clanManager.getPlayer(killer.getName(), false);
            kPlayer.incrementKills();
            kPlayer.alterPoints(cpLayer);

            /** HS Rate System */
            int HSR = (int) (0.08 * cpLayer.getHSRate());
            cpLayer.alterHSRate(-HSR);
            kPlayer.alterHSRate(HSR);
            /** HS Rate System */

            clanManager.updatePlayer(kPlayer);


            /* TODO Tournament Feature */
            if (HSClans.instance.getSettings().getBoolean("tournament.enable")) {
                if (kPlayer.hasClan() && cpLayer.hasClan()) {
                    Clan clan = kPlayer.getClan();
                    clan.incrementPoints();
                    /* Ban player who has been killed, it's hardcore! */
                    event.getEntity().setBanned(true);
                    cpLayer.leaveTournament();
                    event.getEntity().kickPlayer(HSClans.instance.getMessenger().format("messages.errors.tournament-died"));
                    HSClans.instance.getDebug().info("[TOURNAMENT] " + kPlayer.getName() + " from clan " + clan.getName() + " killed player " + cpLayer.getName() + " from clan " + cpLayer.getClan().getName());
                }
            }
            /* Tournament Feature */
        }

        clanManager.updatePlayer(cpLayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        CPLayer cpLayer = HSClans.instance.getClanManager().getPlayer(event.getPlayer().getName(), true);
        // Make sure player has not got any power while he was dead.
        cpLayer.getPower(true);
    }
}
