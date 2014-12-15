package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Command which deals with player demotion in a clan.
 */
public class DemoteCommand extends BaseCommand {
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
    public DemoteCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();

        CPLayer demoter = clanManager.getPlayer(sender.getName(), true);
        CPLayer player = clanManager.getPlayer(subargs[1], false);

        if (player == null) {
            HSClans.instance.getMessenger().message("commands.demote.player-not-found", sender, subargs[1]);
        } else if (clanManager.areInTheSameClan(demoter, player)) {
            if (demoter.getClanRole().getLevel() > player.getClanRole().getLevel()) {
                if (clanManager.demoteClanPlayer(player)) {
                    HSClans.instance.getMessenger().broadcastToClan("commands.demote.success", demoter.getClan(), demoter.getClanRole().getName(),
                            demoter.getName(), player.getName(), player.getClanRole().getName());
                } else {
                    HSClans.instance.getMessenger().message("commands.demote.lowest-rank", sender, player.getName(), player.getClanRole().getName());
                }
            } else {
                HSClans.instance.getMessenger().message("commands.demote.low-rank", sender, player.getName(), player.getClanRole().getName(), demoter.getClanRole().getName());
            }
        } else {
            HSClans.instance.getMessenger().message("commands.demote.not-in-same-clan", sender, player.getName());
        }
    }
}
