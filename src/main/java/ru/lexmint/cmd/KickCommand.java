package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;
import ru.lexmint.listener.ExploitListener;

/**
 * Command which is used for kicking players out of a clan by moderator or leader.
 */
public class KickCommand extends BaseCommand {
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
    public KickCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
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
