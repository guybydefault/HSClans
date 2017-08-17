package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

/**
 * Join the clan.
 */
class JoinCommand extends AbstractClanPlayerCommand {


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
    public JoinCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        if (!cpLayer.hasClan()) {
            Clan clan = clanManager.getClan(subargs[1]);
            if (clan != null) {
                if (clan.pullInvitation(sender.getName()) || BypassCommand.isBypassing(sender.getName())) {
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
