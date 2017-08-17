package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanRole;

import java.util.Set;

/**
 * Send a message to ally's chat.
 */
class AllyChatCommand extends AbstractClanPlayerCommand {


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
    public AllyChatCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        StringBuilder msgSb = new StringBuilder(subargs[1]);
        for (int i = 2; i < subargs.length; i++) {
            msgSb.append(' ').append(subargs[i]);
        }
        String msg = msgSb.toString();
        CPLayer cpLayer = HSClans.instance.getClanManager().getPlayer(sender.getName(), true);
        Clan clan = cpLayer.getClan();
        Set<Player> recipients = clan.getMembersOnline();
        for (Clan ally : clan.getAlliances()) {
            recipients.addAll(ally.getMembersOnline());
        }
        HSClans.instance.getMessenger().chatToAlly(msg, cpLayer, recipients);

    }
}
