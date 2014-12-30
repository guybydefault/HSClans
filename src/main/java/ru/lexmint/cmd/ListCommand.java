package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shows list of all clans on server by their power, rating, etc.
 */
public class ListCommand extends BaseCommand {

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
    public ListCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        int pageNumber = 1;
        int clansPerPage = 7;

        if (subargs.length >= 2) {
            try {
                pageNumber = Integer.valueOf(subargs[1]);
            } catch (NumberFormatException exc) {
                HSClans.instance.getMessenger().message("commands.list.wrong-page-number", sender);
            }
        }

        List<Clan> clanList = new ArrayList<>(HSClans.instance.getClanManager().getClans());

        /** Sorting by HSR **/
        Collections.sort(clanList, new Comparator<Clan>() {
            @Override
            public int compare(Clan o1, Clan o2) {
                if (o1.getHSRate() < o2.getHSRate()) {
                    return 1;
                } else if (o1.getHSRate() > o2.getHSRate()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        /** Sorting by number of members online **/
        Collections.sort(clanList, new Comparator<Clan>() {
            @Override
            public int compare(Clan o1, Clan o2) {
                if (o1.getMembersOnline().size() < o2.getMembersOnline().size()) {
                    return 1;
                } else if (o1.getMembersOnline().size() > o2.getMembersOnline().size()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        HSClans.instance.getMessenger().message("commands.list.header", sender, String.valueOf(pageNumber),
                ((clanList.size() % clansPerPage) > 0)
                        ? String.valueOf(clanList.size() / clansPerPage + 1)
                        : String.valueOf(clanList.size() / clansPerPage));
        for (int i = (pageNumber - 1) * clansPerPage; i < pageNumber * clansPerPage && i < clanList.size(); i++) {
            Clan clan = clanList.get(i);
            HSClans.instance.getMessenger().message("commands.list.clan", sender,
                    clan.getName(),
                    String.valueOf(clan.getMembersOnline().size()),
                    String.valueOf(clan.getMembersSize()),
                    String.valueOf(clan.getClaimsNumber()),
                    String.valueOf(clan.getPowerRounded()),
                    String.valueOf(clan.getPowerMaxRounded()),
                    clan.getLevel().getName());
        }
    }
}
