package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;
import ru.lexmint.hsclans.listener.ExploitListener;

/**
 * Command which is used for kicking players out of a clan by moderator or leader.
 */
class KickCommand extends AbstractClanPlayerCommand {


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
    public KickCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
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

        String playerName = subargs[1];
        ClanManager clanManager = HSClans.instance.getClanManager();

        final CPLayer player = clanManager.getPlayer(playerName, false);
        final CPLayer kicker = clanManager.getPlayer(sender.getName(), true);
        Clan clan = kicker.getClan();
        if (player == null) {
            HSClans.instance.getMessenger().message("commands.kick.player-not-found", sender, subargs[1]);
        } else if (clanManager.areInTheSameClan(player, kicker)) {
            if (kicker.getClanRole().getLevel() > player.getClanRole().getLevel() || BypassCommand.isBypassing(sender.getName())) {
                clanManager.removePlayerFromClan(player);
                if (clan.hasLeader()) {
                    HSClans.instance.getMessenger().broadcastToClan("commands.kick.success", clan, kicker.getClanRole().getName(), kicker.getName(),
                            player.getClanRole().getName(), player.getName());
                    /* Fine to clan's power if cPlayer has negative power. */
                    ExploitListener.handlePowerLeaveExploit(clan, player);
                } else {
                    HSClans.instance.getMessenger().broadcastToAll("commands.kick.disband-broadcast", kicker.getName(), clan.getName());
                }
            } else {
                HSClans.instance.getMessenger().message("commands.kick.low-rank", sender, player.getName(), player.getClanRole().getName(),
                        kicker.getClanRole().getName());
            }
        } else {
            HSClans.instance.getMessenger().message("commands.kick.not-in-same-clan", sender, player.getName());
        }
    }
}
