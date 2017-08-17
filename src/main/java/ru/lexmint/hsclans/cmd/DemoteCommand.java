package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

/**
 * Command which deals with player demotion in a clan.
 */
class DemoteCommand extends AbstractClanPlayerCommand {


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
    public DemoteCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();

        CPLayer demoter = clanManager.getPlayer(sender.getName(), true);
        CPLayer player = clanManager.getPlayer(subargs[1], false);

        if (player == null) {
            HSClans.instance.getMessenger().message("commands.demote.player-not-found", sender, subargs[1]);
        } else if (clanManager.areInTheSameClan(demoter, player)) {
            if (demoter.getClanRole().getLevel() > player.getClanRole().getLevel() || BypassCommand.isBypassing(sender.getName())) {
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
