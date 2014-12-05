package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Leave a clan.
 */
public class LeaveCommand extends BaseCommand {
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
    public LeaveCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);

        String clanName = cpLayer.getClan().getName();

        if (cpLayer.getClanRole() == ClanRole.LEADER) {
            HSClans.instance.getMessenger().broadcastToAll("commands.leave.disband-broadcast", sender.getName(), clanName);
        } else {
            HSClans.instance.getMessenger().broadcastToClan("commands.leave.clan-broadcast", clanName, sender.getName(), clanName);
        }

        clanManager.removePlayerFromClan(sender.getName());
    }
}
