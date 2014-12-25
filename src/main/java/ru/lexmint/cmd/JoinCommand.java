package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Join the clan.
 */
public class JoinCommand extends BaseCommand {
    /**
     * Main constructor for creating a command.
     *
     * @param senderIsPlayer   Is sender required to be a player or not.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param usage            String which contains information how to use this command.
     */
    public JoinCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        if (cpLayer.getClanRole() == ClanRole.OUTLAW) {
            Clan clan = clanManager.getClan(subargs[1]);
            if (clan != null) {
                if (clan.getClanLeague() != cpLayer.getClanLeague()) {
                    HSClans.instance.getMessenger().message("commands.join.wrong-league", sender, clan.getClanLeague().getName());
                } else if (clan.pullInvitation(sender.getName())) {
                    clanManager.addPlayerToClan(clan, cpLayer, ClanRole.NEWBIE);
                    HSClans.instance.getMessenger().broadcastToClan("commands.join.broadcast-to-clan", clan, sender.getName(), clan.getName());
                } else {
                    HSClans.instance.getMessenger().message("commands.join.not-invited", sender, clan.getName());
                }
            } else {
                HSClans.instance.getMessenger().message("commands.join.clan-not-found", sender, subargs[1]);
            }
        } else {
            HSClans.instance.getMessenger().message("commands.join.already-in-clan", sender, cpLayer.getClan().getName());
        }
    }
}
