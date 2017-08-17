package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.ClanRole;

import java.util.HashSet;
import java.util.Set;

/**
 * Command which enabled auto claim mode (when player has this mode enabled, he will
 * try to claim the land which he stands on or moves to as he was constantly entering claim
 * command.
 */
public class AutoclaimCommand extends AbstractClanPlayerCommand {


    /**
     * Set which stores names of players who have enabled autoclaim mode.
     */
    private static final Set<String> autoclaimPlayers = new HashSet<>();

    /**
     * Main constructor for creating a command.
     *
     * @param aliases
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param usage            String which contains information how to use this command.
     */
    public AutoclaimCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
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
    public void perform(Player sender, String[] subargs) {
        if (isAutoclaiming(sender.getName())) {
            autoclaimPlayers.remove(sender.getName());
            HSClans.instance.getMessenger().message("commands.autoclaim.disabled", sender);
        } else {
            autoclaimPlayers.add(sender.getName());
            HSClans.instance.getMessenger().message("commands.autoclaim.enabled", sender);
        }
    }
}
