package ru.lexmint.hsclans.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hscore.cmd.AbstractCommand;

import java.util.Set;

/**
 * Shows help of the plugin.
 */
class HelpCommand extends AbstractCommand {

    private final String[] helpList;

    public HelpCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
        Set<String> rawHelpList = HSClans.instance.getLangConfig().getConfigurationSection("commands.help.list").getKeys(false);
        helpList = new String[rawHelpList.size()];
        rawHelpList.toArray(helpList);
    }


    @Override
    public void perform(CommandSender sender, String[] subargs) {
        int pageNumber = 1;
        int helpLinesPerPage = 7;

        if (subargs.length >= 2) {
            try {
                pageNumber = Integer.valueOf(subargs[1]);
                if (pageNumber <= 0) {
                    HSClans.instance.getMessenger().message("commands.help.wrong-page-number", sender);
                    return;
                }
            } catch (NumberFormatException exc) {
                HSClans.instance.getMessenger().message("commands.help.wrong-page-number", sender);
                return;
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
