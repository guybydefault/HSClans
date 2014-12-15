package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Commands which deals with player promotion in a clan.
 */
public class PromoteCommand extends BaseCommand {
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
    public PromoteCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();

        CPLayer promoter = clanManager.getPlayer(sender.getName(), true);
        CPLayer player = clanManager.getPlayer(subargs[1], false);

        if (player == null) {
            HSClans.instance.getMessenger().message("commands.promote.player-not-found", sender, subargs[1]);
        } else if (clanManager.areInTheSameClan(promoter, player)) {
            if (promoter.getClanRole().getLevel() > player.getClanRole().getLevel()) {
                if (clanManager.promoteClanPlayer(player)) {
                    HSClans.instance.getMessenger().broadcastToClan("commands.promote.success", promoter.getClan(), promoter.getClanRole().getName(),
                            promoter.getName(), player.getName(), player.getClanRole().getName());
                } else {
                    HSClans.instance.getMessenger().message("commands.promote.highest-rank", sender, player.getName(), player.getClanRole().getName());
                }
            } else {
                HSClans.instance.getMessenger().message("commands.promote.low-rank", sender, player.getName(), player.getClanRole().getName(), promoter.getClanRole().getName());
            }
        } else {
            HSClans.instance.getMessenger().message("commands.promote.not-in-same-clan", sender, player.getName());
        }
    }
}
