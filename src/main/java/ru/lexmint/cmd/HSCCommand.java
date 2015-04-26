package ru.lexmint.cmd;


import ru.lexmint.commands.BaseCommand;
import ru.lexmint.domain.ClanRole;

/**
 * Class describing basic command.
 */
public abstract class HSCCommand extends BaseCommand {

    /**
     * Minimum required clan role for executing this command.
     */
    protected final ClanRole requiredClanRole;

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
    public HSCCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, permission, arguments, usage);
        this.requiredClanRole = requiredClanRole;
    }


    /**
     * Returns minimal required clan role for executing this command by a player.
     *
     * @return Minimal required ClanRole.
     */
    public final ClanRole getRequiredClanRole() {
        return requiredClanRole;
    }
}
