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
     * Required permission for executing this command.
     */
    protected final String permission;

    /**
     * Is sender required to be a player or not.
     */
    protected boolean senderIsPlayer;

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
    public BaseCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        this.requiredClanRole = requiredClanRole;
        this.arguments = arguments;
        this.permission = permission;
        this.usage = usage;
        this.senderIsPlayer = senderIsPlayer;
    }

    /**
     * Method which is called when player entered the command (with required number of sub arguments and clan role).
     *
     * @param sender  Sender of the command.
     * @param subargs Sub arguments to the command.
     */
    public abstract void perform(CommandSender sender, String[] subargs);

    /**
     * Returns usage of this command.
     *
     * @return Message showing right syntax of the command.
     */
    public final String getUsage() {
        return usage;
    }

    /**
     * Returns required permissions for executing this command.
     *
     * @return String containing permission.
     */
    public final String getPermission() {
        return permission;
    }

    /**
     * Returns whether sender is required to be a player or not.
     *
     * @return True if sender has to be a player, otherwise - false (for console, for example).
     */
    public final boolean getSenderIsPlayer() {
        return senderIsPlayer;
    }

    /**
     * Returns minimal required clan role for executing this command by a player.
     * @return Minimal required ClanRole.
     */
    public final ClanRole getRequiredClanRole() {
        return requiredClanRole;
    }

    /**
     * Returns minimal required number of arguments for executing this command.
     * @return Minimal required number of arguments.
     */
    public final int getArguments() {
        return arguments;
    }
}
