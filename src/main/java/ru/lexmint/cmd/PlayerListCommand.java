package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shows list of clan players by rating.
 */
public class PlayerListCommand extends HSCCommand {
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
    public PlayerListCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        int pageNumber = 1;
        int playersPerPage = 7;

        if (subargs.length >= 2) {
            try {
                pageNumber = Integer.valueOf(subargs[1]);
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
