package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.ClanRole;

import java.util.Set;

/**
 * Shows help of the plugin.
 */
public class HelpCommand extends BaseCommand {
    /**
     * Main constructor for creating a command.
     *
     * @param senderIsPlayer   Is sender required to be a player or not.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param usage            String which contains information how to use this command.
     */
    public HelpCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
        Set<String> rawHelpList = HSClans.instance.getLangConfig().getConfigurationSection("commands.help.list").getKeys(false);
        helpList = new String[rawHelpList.size()];
        rawHelpList.toArray(helpList);
    }

    private final String[] helpList;

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        int pageNumber = 1;
        int helpLinesPerPage = 7;

        if (subargs.length >= 2) {
            try {
                pageNumber = Integer.valueOf(subargs[1]);
            } catch (NumberFormatException exc) {
                HSClans.instance.getMessenger().message("commands.help.wrong-page-number", sender);
            }
        }

        HSClans.instance.getMessenger().message("commands.help.header", sender, String.valueOf(pageNumber),
                ((helpList.length % helpLinesPerPage) > 0)
                        ? String.valueOf(helpList.length / helpLinesPerPage + 1)
                        : String.valueOf(helpList.length / helpLinesPerPage));
        for (int i = (pageNumber - 1) * helpLinesPerPage; i < pageNumber * helpLinesPerPage && i < helpList.length; i++) {
            HSClans.instance.getMessenger().message("commands.help.list." + helpList[i], sender);

        }
    }
}
