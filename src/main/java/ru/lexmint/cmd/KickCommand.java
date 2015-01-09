package ru.lexmint.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Command which is used for kicking players out of a clan by moderator or leader.
 */
public class KickCommand extends BaseCommand {
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
    public KickCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        String playerName = subargs[1];
        ClanManager clanManager = HSClans.instance.getClanManager();

        final CPLayer player = clanManager.getPlayer(playerName, false);
        final CPLayer kicker = clanManager.getPlayer(sender.getName(), true);
        if (player == null) {
            HSClans.instance.getMessenger().message("commands.kick.player-not-found", sender, subargs[1]);
        } else if (clanManager.areInTheSameClan(player, kicker)) {
            if (kicker.getClanRole().getLevel() > player.getClanRole().getLevel() || BypassCommand.isBypassing(sender.getName())) {
                clanManager.removePlayerFromClan(player);
                HSClans.instance.getMessenger().broadcastToClan("commands.kick.success", kicker.getClan(), kicker.getClanRole().getName(), kicker.getName(),
                        player.getClanRole().getName(), player.getName());
                final double power = player.getPower();
                if (power < 0) {
                    double minutes = -power / HSClans.instance.getSettings().getDouble("power.per-minute");
                    kicker.getClan().alterPowerBoost(power);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(HSClans.instance, new Runnable() {
                        @Override
                        public void run() {
                            kicker.getClan().alterPowerBoost(-power);
                        }
                    }, (int) (20 * 60 * minutes));
                    HSClans.instance.getMessenger().broadcastToClan("commands.kick.clan-fine", kicker.getClan(), String.valueOf((int) power), String.valueOf(Math.round(minutes)), kicker.getName());
                }
            } else {
                HSClans.instance.getMessenger().message("commands.kick.low-rank", sender, player.getName(), player.getClanRole().getName(),
                        kicker.getClanRole().getName());
            }
        } else {
            HSClans.instance.getMessenger().message("commands.kick.not-in-same-clan", sender, player.getName());
        }
    }
}
