package ru.lexmint.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanRole;

/**
 * Author: lexmint.
 * Created for: HSArena.
 * Date: 11.08.15 (21:36).
 */
public class TournamentCommand extends HSCCommand {
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
    public TournamentCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender commandSender, String[] strings) {
        boolean tournamentEnabled = HSClans.instance.getSettings().getBoolean("tournament.enable");
        if (tournamentEnabled) {
            HSClans.instance.getSettings().set("tournament.enable", false);
            HSClans.instance.getClanManager().disableTournament();
            HSClans.instance.getSettings().saveConfig();
            HSClans.instance.getMessenger().message("commands.tournament.disabled", commandSender);
        } else {
            HSClans.instance.getSettings().set("tournament.enable", true);
            HSClans.instance.getClanManager().enableTournament();
            HSClans.instance.getSettings().saveConfig();
            HSClans.instance.getMessenger().message("commands.tournament.enabled", commandSender);

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                CPLayer cpLayer = HSClans.instance.getClanManager().getPlayer(player.getName(), false);
                if (!cpLayer.hasClan() && !player.hasPermission("hsclans.command.bypass")) {
                    player.kickPlayer(HSClans.instance.getMessenger().format("messages.errors.tournament-join"));
                }
            }
        }
    }
}
