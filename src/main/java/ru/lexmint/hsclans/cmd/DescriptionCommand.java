package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command which sets description to clan.
 */
class DescriptionCommand extends AbstractClanPlayerCommand {


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
    public DescriptionCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        StringBuilder description = new StringBuilder();
        Pattern pattern = Pattern.compile("[А-Яа-я!.,; ]+");
        for (int i = 1; i < subargs.length; i++) {
            Matcher matcher = pattern.matcher(subargs[i]);
            if (matcher.matches()) {
                description.append(subargs[i]);
                if (i < subargs.length - 1) {
                    description.append(" ");
                }
            } else {
                HSClans.instance.getMessenger().message("commands.description.wrong-desc", sender);
                return;
            }
        }

        String descriptionStr = description.toString();
        if (descriptionStr.length() > 92) {
            HSClans.instance.getMessenger().message("commands.description.long-desc", sender);
            return;
        }

        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        Clan clan = cpLayer.getClan();
        clan.setDescription(descriptionStr);
        HSClans.instance.getMessenger().broadcastToClan("commands.description.changed", clan, cpLayer.getClanRole().getName(),
                cpLayer.getName(), descriptionStr);
        clanManager.updateClan(clan);
    }
}
