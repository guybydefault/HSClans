package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.domain.ClanRole;

/**
 * Shows help of the plugin.
 */
public class HelpCommand extends BaseCommand {
    /**
     * Main constructor for creating a command.
     *
     * @param senderIsPlayer Is sender required to be a player or not.
     * @param permission Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param usage            String which contains information how to use this command.
     */
    public HelpCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        sender.sendMessage("Help command");

    }
}
