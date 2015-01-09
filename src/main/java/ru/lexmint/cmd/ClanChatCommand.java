package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanRole;

import java.util.Set;

/**
 * Send a message to clan's chat.
 */
public class ClanChatCommand extends BaseCommand {
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
    public ClanChatCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        StringBuilder msgSb = new StringBuilder(subargs[1]);
        for (int i = 2; i < subargs.length; i++) {
            msgSb.append(' ').append(subargs[i]);
        }
        String msg = msgSb.toString();
        CPLayer cpLayer = HSClans.instance.getClanManager().getPlayer(sender.getName(), true);
        Clan clan = cpLayer.getClan();
        Set<Player> recipients = clan.getMembersOnline();
        HSClans.instance.getMessenger().chatToClan(msg, cpLayer, recipients);
    }
}
