package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;
import ru.lexmint.listener.ExploitListener;

/**
 * Leave a clan.
 */
public class LeaveCommand extends HSCCommand {
    /**
     * Main constructor for creating a command.
     *
     * @param senderIsPlayer   Is sender required to be a player or not.
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param usage            String which contains information how to use this command.
     */
    public LeaveCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
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
