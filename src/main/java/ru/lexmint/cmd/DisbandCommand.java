package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Command which is used by admins to disband clans.
 */
public class DisbandCommand extends HSCCommand {
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
    public DisbandCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        Clan clan = clanManager.getClan(subargs[1]);
        if (clan != null) {
            HSClans.instance.getMessenger().broadcastToAll("commands.disband.success", sender.getName(), clan.getName());
            clanManager.removeClan(clan);
        } else {
            HSClans.instance.getMessenger().message("commands.disband.not-found", sender, subargs[1]);
        }
    }
}
