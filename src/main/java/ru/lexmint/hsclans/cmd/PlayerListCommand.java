package ru.lexmint.hsclans.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hscore.cmd.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shows list of clan players by rating.
 */
class PlayerListCommand extends AbstractCommand {


    public PlayerListCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        int pageNumber = 1;
        int playersPerPage = 7;

        if (subargs.length >= 2) {
            try {
                pageNumber = Integer.valueOf(subargs[1]);
                if (pageNumber <= 0) {
                    HSClans.instance.getMessenger().message("commands.help.wrong-page-number", sender);
                    return;
                }
            } catch (NumberFormatException exc) {
                HSClans.instance.getMessenger().message("commands.player-list.wrong-page-number", sender);
            }
        }

        List<CPLayer> playersList = new ArrayList<>(HSClans.instance.getClanManager().getClanPlayers());

        /** Sorting by HSR **/
        Collections.sort(playersList, new Comparator<CPLayer>() {
            @Override
            public int compare(CPLayer o1, CPLayer o2) {
                if (o1.getHSRateView() < o2.getHSRateView()) {
                    return 1;
                } else if (o1.getHSRateView() > o2.getHSRateView()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });


        HSClans.instance.getMessenger().message("commands.player-list.header", sender, String.valueOf(pageNumber),
                ((playersList.size() % playersPerPage) > 0)
                        ? String.valueOf(playersList.size() / playersPerPage + 1)
                        : String.valueOf(playersList.size() / playersPerPage));
        for (int i = (pageNumber - 1) * playersPerPage; i < pageNumber * playersPerPage && i < playersList.size(); i++) {
            CPLayer cpLayer = playersList.get(i);
            String name;
            if (cpLayer.isOnline()) {
                name = HSClans.instance.getMessenger().format("commands.player-list.status.online", cpLayer.getName());
            } else {
                name = HSClans.instance.getMessenger().format("commands.player-list.status.offline", cpLayer.getName());
            }
            HSClans.instance.getMessenger().message("commands.player-list.player", sender,
                    String.valueOf(i + 1),
                    name,
                    String.valueOf(cpLayer.getHoursPlayedWeekRounded()),
                    String.valueOf(cpLayer.getHoursPlayedTotalRounded()),
                    cpLayer.getLevel().getName(),
                    String.valueOf(cpLayer.getHSRateView()));
        }
    }
}
