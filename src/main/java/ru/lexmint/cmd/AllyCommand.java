package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Creates an alliance with
 */
public class AllyCommand extends HSCCommand {
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
    public AllyCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        Clan clan = cpLayer.getClan();

        Clan ally = clanManager.getClan(subargs[1]);
        if (ally == null) {
            HSClans.instance.getMessenger().message("commands.ally.not-found", sender, subargs[1]);
        } else if (ally == clan) {
            HSClans.instance.getMessenger().message("commands.ally.same-clan", sender);
        } else if (clan.isAlliedWith(ally)) {
            clan.removeAlliance(ally);
            ally.removeAlliance(clan);
            clanManager.updateClan(clan);
            clanManager.updateClan(ally);
            HSClans.instance.getMessenger().broadcastToClan("commands.ally.unsigned-clan", clan, cpLayer.getClanRole().getName(), cpLayer.getName(), ally.getName());
            HSClans.instance.getMessenger().broadcastToClan("commands.ally.unsigned", ally, cpLayer.getClanRole().getName(), cpLayer.getName(), clan.getName());
        } else if (clan.isRequestingAllyWith(ally)) {
            clan.removeAlliance(ally);
            clanManager.updateClan(clan);
            HSClans.instance.getMessenger().broadcastToClan("commands.ally.recall", ally, cpLayer.getClanRole().getName(),
                    cpLayer.getName(), clan.getName());
            HSClans.instance.getMessenger().broadcastToClan("commands.ally.recall-clan", clan, cpLayer.getClanRole().getName(), cpLayer.getName(), ally.getName());
        } else {
            if (clan.addAlliance(ally)) {
                if (clan.isAlliedWith(ally)) {
                    HSClans.instance.getMessenger().broadcastToClan("commands.ally.formed", ally, cpLayer.getClanRole().getName(),
                            cpLayer.getName(), clan.getName());
                    HSClans.instance.getMessenger().broadcastToClan("commands.ally.formed-clan", clan, cpLayer.getClanRole().getName(),
                            cpLayer.getName(), ally.getName());
                } else {
                    HSClans.instance.getMessenger().broadcastToClan("commands.ally.suggest", ally, cpLayer.getClanRole().getName(), cpLayer.getName(), clan.getName());
                    HSClans.instance.getMessenger().broadcastToClan("commands.ally.suggest-clan", clan, cpLayer.getClanRole().getName(),
                            cpLayer.getName(), ally.getName());
                }
            } else {
                HSClans.instance.getMessenger().message("commands.ally.limit", sender);
            }
        }
    }
}
