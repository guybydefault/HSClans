package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;
import ru.lexmint.utils.Messenger;

import java.util.*;

/**
 * Command which delivers information about the clan/player to the sender.
 */
public class ShowCommand extends BaseCommand {

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
    public ShowCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        Messenger messenger = HSClans.instance.getMessenger();

        Clan clan;
        if (subargs.length >= 2) {
            clan = clanManager.getClan(subargs[1]);
            if (clan == null) {
                CPLayer cpLayer = clanManager.getPlayer(subargs[1], false);
                if (cpLayer != null) {
                    clan = cpLayer.getClan();
                }
            }
        } else {
            if (sender instanceof Player) {
                clan = clanManager.getPlayer(sender.getName(), true).getClan();
            } else {
                messenger.message("commands.show.console-without-arg", sender);
                return;
            }
        }

        if (clan == null) {
            messenger.message("commands.show.not-found", sender);
            return;
        }

        String description = clan.getDescription();
        if (description == null) {
            description = HSClans.instance.getLangConfig().getString("commands.show.default-description");
        }

        StringBuilder allies = new StringBuilder();
        Iterator<Clan> allyIt = clan.getAlliances().iterator();
        while (allyIt.hasNext()) {
            Clan ally = allyIt.next();
            allies.append(messenger.format("commands.show.allie", ally.getName()));
            if (allyIt.hasNext()) {
                allies.append(", ");
            }
        }

        Set<CPLayer> membersSet = clan.getCMembers();
        List<CPLayer> membersList = new LinkedList<>(membersSet);
        Collections.sort(membersList, new Comparator<CPLayer>() {
            @Override
            public int compare(CPLayer o1, CPLayer o2) {
                if (o1.isOnline() && !o2.isOnline()) {
                    return -1;
                } else if (o2.isOnline() && !o1.isOnline()) {
                    return 1;
                } else {
                    return compareRank(o1, o2);
                }
            }

            private int compareRank(CPLayer o1, CPLayer o2) {
                if (o1.getClanRole().getLevel() == o2.getClanRole().getLevel()) {
                    return 0;
                } else if (o2.getClanRole().getLevel() > o1.getClanRole().getLevel()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        StringBuilder members = new StringBuilder();
        Iterator<CPLayer> memberIt = membersList.iterator();
        while (memberIt.hasNext()) {
            CPLayer member = memberIt.next();
            if (member.isOnline()) {
                members.append(messenger.format("commands.show.member.online", member.getClanRole().getName(), member.getName()));
            } else {
                members.append(messenger.format("commands.show.member.offline", member.getClanRole().getName(), member.getName()));
            }
            if (memberIt.hasNext()) {
                members.append(", ");
            }
        }

        messenger.message("commands.show.header", sender, clan.getName());
        messenger.message("commands.show.age", sender, String.valueOf(clan.getDaysSinceCreated()));
        messenger.message("commands.show.description", sender, description);
        messenger.message("commands.show.power-and-land", sender, String.valueOf(clan.getClaimsNumber()), String.valueOf(clan.getPowerRounded()), String.valueOf(clan.getPowerMaxRounded()), String.valueOf(clan.getMembersSize()));
        if (!allies.toString().isEmpty()) {
            messenger.message("commands.show.alliances", sender, allies.toString());
        } else {
            messenger.message("commands.show.no-allies", sender);
        }
        messenger.message("commands.show.level", sender, clan.getLevel().getName(), String.valueOf(clan.getHSRate(2)));
        messenger.message("commands.show.size", sender, String.valueOf(clan.getMembersSize()), String.valueOf(clan.getMembersOnline().size()));
        messenger.message("commands.show.members", sender, members.toString());
    }
}
