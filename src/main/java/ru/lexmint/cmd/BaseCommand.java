package ru.lexmint.cmd;


import org.bukkit.command.CommandSender;
import ru.lexmint.domain.ClanRole;

/**
 * Class describing basic command.
 */
public abstract class BaseCommand {
    /**
     * Required number of arguments for this command.
     */
    protected final int arguments;

    /**
     * Minimum required clan role for executing this command.
     */
    protected final ClanRole requiredClanRole;

    /**
     * Message showing right syntax of the command.
     */
    protected final String usage;

    /**
     * Main constructor for creating a command.
     * @param requiredClanRole Minimal role in a clan for executing the command.
     * @param arguments Minimal number of sub arguments (command label is not included),
     *                  required for executing the command.
     */
    public BaseCommand(ClanRole requiredClanRole, int arguments, String usage) {
        this.arguments = arguments;
        this.requiredClanRole = requiredClanRole;
        this.usage = usage;
    }

    /**
     * Method which is called when player entered the command (with required number of sub arguments and clan role).
     * @param sender Sender of the command.
     * @param subargs Sub arguments to the command.
     */
    public abstract void perform(CommandSender sender, String[] subargs);

    /**
     * Return usage of this command.
     * @return Message showing right syntax of the command.
     */
    public String getUsage() {
        return usage;
    }
}
