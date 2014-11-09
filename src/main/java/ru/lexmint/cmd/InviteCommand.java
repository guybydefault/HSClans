package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.domain.ClanRole;

/**
 * InviteCommand used for inviting players to the faction.
 */
public class InviteCommand extends BaseCommand {
    /**
     * Main constructor for creating a command.
     * @param requiredClanRole Minimal role in a clan for executing the command.
     * @param arguments Minimal number of sub arguments (command label is not included),
     *                  required for executing the command.
     */
    InviteCommand(ClanRole requiredClanRole, int arguments, String usage) {
        super(requiredClanRole, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        sender.sendMessage("Invite command");

    }
}
