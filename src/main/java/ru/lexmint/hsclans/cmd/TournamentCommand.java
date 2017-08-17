package ru.lexmint.hsclans.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hscore.cmd.AbstractCommand;

/**
 * Author: lexmint.
 * Created for: HSArena.
 * Date: 11.08.15 (21:36).
 */
class TournamentCommand extends AbstractCommand {


    public TournamentCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
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
