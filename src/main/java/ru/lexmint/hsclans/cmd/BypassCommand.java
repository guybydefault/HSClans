package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.ClanRole;

import java.util.HashSet;
import java.util.Set;

/**
 * Command which is used by admins to bypass clans' claims.
 */
public class BypassCommand extends AbstractClanPlayerCommand {


    /**
     * Set which stores names of players who have enabled bypass mode.
     */
    private static final Set<String> bypassPlayers = new HashSet<>();

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
    public BypassCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }


    /**
     * Checks if player is in admin (bypass) mode and can bypass claim protection, promote/demote players
     * in clans anyway, etc.
     *
     * @param playerName name of the player who will be checked
     * @return True if player is in bypass mode. Otherwise, false.
     */
    public static boolean isBypassing(String playerName) {
        return bypassPlayers.contains(playerName);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        if (isBypassing(sender.getName())) {
            bypassPlayers.remove(sender.getName());
            HSClans.instance.getMessenger().message("commands.bypass.disabled", sender);
        } else {
            bypassPlayers.add(sender.getName());
            HSClans.instance.getMessenger().message("commands.bypass.enabled", sender);
        }
    }

    public static void removeBypass(String playerName) {
        bypassPlayers.remove(playerName);
    }
}
