package ru.lexmint.hsclans.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hscore.cmd.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shows list of all clans on server by their power, rating, etc.
 */
class ListCommand extends AbstractCommand {


    public ListCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        int pageNumber = 1;
        int clansPerPage = 7;

        if (subargs.length >= 2) {
            try {
                pageNumber = Integer.valueOf(subargs[1]);
                if (pageNumber <= 0) {
                    HSClans.instance.getMessenger().message("commands.help.wrong-page-number", sender);
                    return;
                }
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
        /** Sorting by HSR **/

        /** Sorting by points (Tournament feature) **/
        if (HSClans.instance.getSettings().getBoolean("tournament.enable")) {
            Collections.sort(clanList, new Comparator<Clan>() {
                @Override
                public int compare(Clan o1, Clan o2) {
                    if (o1.getPoints() < o2.getPoints()) {
                        return 1;
                    } else if (o1.getPoints() > o2.getPoints()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        /** Sorting by points **/

        if (HSClans.instance.getSettings().getBoolean("tournament.enable")) {
            HSClans.instance.getMessenger().message("commands.list.header-tournament", sender, String.valueOf(pageNumber),
                    ((clanList.size() % clansPerPage) > 0)
                            ? String.valueOf(clanList.size() / clansPerPage + 1)
                            : String.valueOf(clanList.size() / clansPerPage));
            for (int i = (pageNumber - 1) * clansPerPage; i < pageNumber * clansPerPage && i < clanList.size(); i++) {
                Clan clan = clanList.get(i);
                HSClans.instance.getMessenger().message("commands.list.clan-tournament", sender,
                        clan.getName(),
                        String.valueOf(clan.getMembersOnline().size()),
                        String.valueOf(clan.getMembersSize()),
                        String.valueOf(clan.getClaimsNumber()),
                        String.valueOf(clan.getPowerRounded()),
                        String.valueOf(clan.getPowerMaxRounded()),
                        String.valueOf(clan.getPoints()));
            }
        } else {
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
}
