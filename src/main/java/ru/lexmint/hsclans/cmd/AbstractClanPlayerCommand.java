package ru.lexmint.hsclans.cmd;


import ru.lexmint.hsclans.domain.ClanRole;
import ru.lexmint.hscore.cmd.AbstractPlayerCommand;

/**
 * Class describing basic command.
 */
abstract class AbstractClanPlayerCommand extends AbstractPlayerCommand {

    private final ClanRole requiredClanRole;

    /**
     * Main constructor for creating a command.
     *
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param usage            String which contains information how to use this command.
     */
    AbstractClanPlayerCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
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
