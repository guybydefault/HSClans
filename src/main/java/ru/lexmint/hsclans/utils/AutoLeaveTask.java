package ru.lexmint.hsclans.utils;

import org.bukkit.scheduler.BukkitRunnable;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

import java.util.List;

/**
 * This task automatically iterates through all of clan players and if they
 * have been inactive for defined (in configuration) amount of time, they
 * will be kicked from their clan.
 */
public class AutoLeaveTask extends BukkitRunnable {

    @Override
    public void run() {
        ClanManager clanManager = HSClans.instance.getClanManager();
        List<CPLayer> playerList = clanManager.getClanPlayers();
        double hoursInactive = HSClans.instance.getSettings().getDouble("player.auto-leave.hours-inactive");
        for (CPLayer cpLayer : playerList) {
            if (cpLayer.isOnline() || !cpLayer.hasClan()) {
                continue;
            }
            if (cpLayer.getHoursSinceLastPlayed() > hoursInactive) {
                HSClans.instance.getDebug().info("Player " + cpLayer.getName() + " kicked from clan " + cpLayer.getClan().getName() + " for inactivity (" + hoursInactive + "h)");
                kickFromClan(clanManager, cpLayer);
            }
        }
    }

    /**
     * Kicks player from clan for inactivity.
     *
     * @param clanManager Clan manager
     * @param cpLayer     CPlayer which will be removed from clan.
     */
    private void kickFromClan(ClanManager clanManager, CPLayer cpLayer) {
        Clan clan = cpLayer.getClan();
        if (cpLayer.getClanRole() == ClanRole.LEADER) {
            if (clan.getMembersSize() >= 2) {
                CPLayer newLeader = null;
                for (CPLayer member : clan.getCMembers()) {
                    if (newLeader == null
                            || (member.getClanRole() == ClanRole.MODERATOR && newLeader.getClanRole() != ClanRole.MODERATOR)
                            || member.getHSRateView() > newLeader.getHSRateView()) {
                        newLeader = member;
                    }
                }
                clanManager.setCPlayerRole(newLeader, ClanRole.LEADER);
                HSClans.instance.getMessenger().broadcastToClan("messages.auto-leave.clan-new-leader", clan, cpLayer.getName(), newLeader.getName());
            } else {
                HSClans.instance.getMessenger().broadcastToAll("messages.auto-leave.disband-broadcast", clan.getName(), cpLayer.getName());
            }
        } else {
            HSClans.instance.getMessenger().broadcastToClan("messages.auto-leave.clan-broadcast", clan, cpLayer.getName());
        }
        clanManager.removePlayerFromClan(cpLayer);
    }
}
