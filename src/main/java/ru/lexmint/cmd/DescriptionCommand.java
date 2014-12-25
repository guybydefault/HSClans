package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command which sets description to clan.
 */
public class DescriptionCommand extends BaseCommand {
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
    public DescriptionCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        StringBuilder description = new StringBuilder();
        Pattern pattern = Pattern.compile("[А-Яа-я!.,; ]+");
        for (int i = 1; i < subargs.length; i++) {
            Matcher matcher = pattern.matcher(subargs[i]);
            if (matcher.matches()) {
                description.append(subargs[i]);
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
