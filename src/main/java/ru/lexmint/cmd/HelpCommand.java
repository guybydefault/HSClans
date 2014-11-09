package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.domain.ClanRole;

/**
 * Shows help of the plugin.
 */
public class HelpCommand extends BaseCommand {
    /**
     * Main constructor for creating a command.
     * @param requiredClanRole Minimal role in a clan for executing the command.
     * @param arguments Minimal number of sub arguments (command label is not included),
     *                  required for executing the command.
     */
    HelpCommand(ClanRole requiredClanRole, int arguments, String usage) {
        super(requiredClanRole, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        sender.sendMessage("Help command");

    }
}
