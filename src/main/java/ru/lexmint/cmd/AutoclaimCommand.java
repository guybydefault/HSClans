package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.ClanRole;

import java.util.HashSet;
import java.util.Set;

/**
 * Command which enabled auto claim mode (when player has this mode enabled, he will
 * try to claim the land which he stands on or moves to as he was constantly entering claim
 * command.
 */
public class AutoclaimCommand extends HSCCommand {
    /**
     * Set which stores names of players who have enabled autoclaim mode.
     */
    private static Set<String> autoclaimPlayers = new HashSet<>();

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
    public AutoclaimCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    /**
     * Checks if player is in autoclaim mode.
     *
     * @param playerName name of the player who will be checked
     * @return True if player is in autoclaim mode. Otherwise, false.
     */
    public static boolean isAutoclaiming(String playerName) {
        return autoclaimPlayers.contains(playerName);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        if (isAutoclaiming(sender.getName())) {
            autoclaimPlayers.remove(sender.getName());
            HSClans.instance.getMessenger().message("commands.autoclaim.disabled", sender);
        } else {
            autoclaimPlayers.add(sender.getName());
            HSClans.instance.getMessenger().message("commands.autoclaim.enabled", sender);
        }
    }
}
