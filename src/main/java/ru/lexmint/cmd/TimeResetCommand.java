package ru.lexmint.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanRole;

/**
 * Created by lexmint on 30.04.15.
 */
public class TimeResetCommand extends HSCCommand {
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
    public TimeResetCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender commandSender, String[] strings) {
        /** Updating sessions and time for online players. **/
        for (Player player : Bukkit.getOnlinePlayers()) {
            CPLayer cPlayer = HSClans.instance.getClanManager().getPlayer(player.getName(), true);
            HSClans.instance.getMonitorListener().updateHoursPlayed(cPlayer);
            HSClans.instance.getMonitorListener().getPlayTimes().put(player.getName(), System.currentTimeMillis());
        }
        HSClans.instance.getClanManager().resetHoursPlayedWeek();
        HSClans.instance.getMessenger().message("commands.time-reset.success", commandSender);
    }
}
