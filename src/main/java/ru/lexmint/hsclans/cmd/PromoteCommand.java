package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

/**
 * Commands which deals with player promotion in a clan.
 */
class PromoteCommand extends AbstractClanPlayerCommand {


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
    public PromoteCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();

        CPLayer promoter = clanManager.getPlayer(sender.getName(), true);
        CPLayer player = clanManager.getPlayer(subargs[1], false);
        Clan clan = promoter.getClan();

        if (player == null) {
            HSClans.instance.getMessenger().message("commands.promote.player-not-found", sender, subargs[1]);
        } else if (clanManager.areInTheSameClan(promoter, player)) {
            if (promoter.getClanRole() == ClanRole.LEADER || promoter.getClanRole().getLevel() > player.getClanRole().getLevel() + 1
                    || BypassCommand.isBypassing(sender.getName())) {
                if (player.getClanRole() == ClanRole.getClanRoleByLevel(ClanRole.LEADER.getLevel() - 1) && clan.getLeadersNumber() + 1 > HSClans.instance.getSettings().getInt("clan.max-leaders")) {
                    HSClans.instance.getMessenger().message("commands.promote.leaders-limit", sender, player.getName(), String.valueOf(HSClans.instance.getSettings().getInt("clan.max-leaders")));
                } else {
                    if (clanManager.promoteClanPlayer(player)) {
                        HSClans.instance.getMessenger().broadcastToClan("commands.promote.success", promoter.getClan(), promoter.getClanRole().getName(),
                                promoter.getName(), player.getName(), player.getClanRole().getName());
                    } else {
                        HSClans.instance.getMessenger().message("commands.promote.highest-rank", sender, player.getName(), player.getClanRole().getName());
                    }
                }
            } else {
                HSClans.instance.getMessenger().message("commands.promote.low-rank", sender, player.getName(), player.getClanRole().getName(), promoter.getClanRole().getName());
            }
        } else {
            HSClans.instance.getMessenger().message("commands.promote.not-in-same-clan", sender, player.getName());
        }
    }
}
