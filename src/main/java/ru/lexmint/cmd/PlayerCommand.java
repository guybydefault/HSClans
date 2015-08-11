package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Command which shows player information about other player.
 */
public class PlayerCommand extends HSCCommand {
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
    public PlayerCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();

        CPLayer cpLayer;
        if (subargs.length >= 2) {
            cpLayer = clanManager.getPlayer(subargs[1], false);
        } else {
            if (sender instanceof Player) {
                cpLayer = clanManager.getPlayer(sender.getName(), true);
            } else {
                HSClans.instance.getMessenger().message("commands.player.console-without-arg", sender);
                return;
            }
        }

        if (cpLayer != null) {
            String name;
            if (cpLayer.isOnline()) {
                name = HSClans.instance.getMessenger().format("commands.player.status.online", cpLayer.getName());
            } else {
                name = HSClans.instance.getMessenger().format("commands.player.status.offline", cpLayer.getName());
            }
            HSClans.instance.getMessenger().message("commands.player.header", sender, name);
            HSClans.instance.getMessenger().message("commands.player.first-played", sender, String.valueOf(cpLayer.getDaysSinceFirstPlayed()));
            HSClans.instance.getMessenger().message("commands.player.time-played", sender, String.valueOf(cpLayer.getHoursPlayedTotalRounded()), String.valueOf(cpLayer.getHoursPlayedWeekRounded()));
            HSClans.instance.getMessenger().message("commands.player.power", sender, String.valueOf(cpLayer.getPowerRounded()), String.valueOf(cpLayer.getPowerMaxRounded()));
            HSClans.instance.getMessenger().message("commands.player.level", sender, cpLayer.getLevel().getName(), String.valueOf(cpLayer.getHSRateView()));
            HSClans.instance.getMessenger().message("commands.player.arena-stats", sender, String.valueOf(cpLayer.getArenaWins()), String.valueOf(cpLayer.getArenaDefeats()));
            if (cpLayer.hasClan()) {
                Clan clan = cpLayer.getClan();
                HSClans.instance.getMessenger().message("commands.player.clan", sender, cpLayer.getClanRole().getName(), clan.getLevel().getName(), clan.getName(),
                        String.valueOf(clan.getMembersOnline().size()), String.valueOf(clan.getMembersSize()));
            } else {
                HSClans.instance.getMessenger().message("commands.player.no-clan", sender, cpLayer.getClanRole().getName());
            }
        } else {
            HSClans.instance.getMessenger().message("commands.player.not-found", sender);
        }
    }
}
