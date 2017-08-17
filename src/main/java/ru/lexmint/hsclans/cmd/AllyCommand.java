package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

/**
 * Creates an alliance with
 */
class AllyCommand extends AbstractClanPlayerCommand {


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
    public AllyCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
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
