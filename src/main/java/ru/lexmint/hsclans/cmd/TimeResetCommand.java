package ru.lexmint.hsclans.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hscore.cmd.AbstractCommand;

/**
 * Created by lexmint on 30.04.15.
 */
class TimeResetCommand extends AbstractCommand {


    public TimeResetCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender commandSender, String[] strings) {
        /** Updating sessions and time for online players. **/
        for (Player player : Bukkit.getOnlinePlayers()) {
            CPLayer cPlayer = HSClans.instance.getClanManager().getPlayer(player.getName(), true);
            HSClans.instance.getMonitorListener().updateHoursPlayed(cPlayer);
            HSClans.instance.getMonitorListener().getPlayTimes().put(player.getName(), System.currentTimeMillis());
        }
        for (CPLayer cpLayer : HSClans.instance.getClanManager().getAllCPLayers()) {
            cpLayer.updateHSRate();
            cpLayer.resetHoursPlayedWeek();
            HSClans.instance.getClanManager().updatePlayer(cpLayer);
        }
//        HSClans.instance.getClanManager().resetHoursPlayedWeek();
        HSClans.instance.getMessenger().message("commands.time-reset.success", commandSender);
    }
}
