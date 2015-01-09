package ru.lexmint.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
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
        final CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        final Clan clan = cpLayer.getClan();
        clanManager.removePlayerFromClan(cpLayer);

        if (clan.hasLeader()) {
            HSClans.instance.getMessenger().message("commands.leave.success", sender, clan.getName());
            HSClans.instance.getMessenger().broadcastToClan("commands.leave.clan-broadcast", clan, cpLayer.getName(), clan.getName());
            /* Fine to clan's power */
            final double power = cpLayer.getPower();
            if (power < 0) {
                double minutes = -power / HSClans.instance.getSettings().getDouble("power.per-minute");
                clan.alterPowerBoost(power);
                Bukkit.getScheduler().scheduleSyncDelayedTask(HSClans.instance, new Runnable() {
                    @Override
                    public void run() {
                        clan.alterPowerBoost(-power);
                    }
                }, (int) (20 * 60 * minutes));
                HSClans.instance.getMessenger().broadcastToClan("commands.leave.clan-fine", clan, String.valueOf((int) cpLayer.getPower()), String.valueOf(Math.round(minutes)), cpLayer.getName());
            }

        } else {
            HSClans.instance.getMessenger().broadcastToAll("commands.leave.disband-broadcast", cpLayer.getName(), clan.getName());
        }
    }
}
