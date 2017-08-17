package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;
import ru.lexmint.hsclans.listener.ExploitListener;

/**
 * Leave a clan.
 */
class LeaveCommand extends AbstractClanPlayerCommand {


    /**
     * Main constructor for creating a command.
     *
     * @param aliases
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param usage            String which contains information how to use this command.
     */
    public LeaveCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        /** TODO Tournament feature */
        if (HSClans.instance.getSettings().getBoolean("tournament.enable") && !sender.hasPermission("hsclans.command.bypass")) {
            HSClans.instance.getMessenger().message("messages.errors.tournament-deny", sender);
            return;
        }
        /* Tournament feature */

        ClanManager clanManager = HSClans.instance.getClanManager();
        final CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        final Clan clan = cpLayer.getClan();
        clanManager.removePlayerFromClan(cpLayer);

        if (clan.hasLeader()) {
            HSClans.instance.getMessenger().message("commands.leave.success", sender, clan.getName());
            HSClans.instance.getMessenger().broadcastToClan("commands.leave.clan-broadcast", clan, cpLayer.getName(), clan.getName());
            /* Fine to clan's power if cPlayer has negative power. */
            ExploitListener.handlePowerLeaveExploit(clan, cpLayer);
        } else {
            HSClans.instance.getMessenger().broadcastToAll("commands.leave.disband-broadcast", cpLayer.getName(), clan.getName());
        }
    }
}
