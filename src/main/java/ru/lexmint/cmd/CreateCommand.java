package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * Command for creating clan.
 */
public class CreateCommand extends HSCCommand {
    /**
     * Main constructor for creating a command.
     *
     * @param senderIsPlayer   Is sender required to be a player or not.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param usage            String which contains information how to use this command.
     */
    public CreateCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        Pattern pattern = Pattern.compile("[A-Z][A-Za-z]+");
        if (!pattern.matcher(subargs[1]).matches() || subargs[1].length() > 8 || subargs[1].length() < 2) {
            HSClans.instance.getMessenger().message("commands.create.wrong-name", sender);
            return;
        }
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        if (cpLayer.getClanRole() == ClanRole.OUTLAW) {
            if (!clanManager.containsClan(subargs[1])) {
                if (cpLayer.getHoursPlayedTotal() >= HSClans.instance.getConfig().getDouble("player.create-hours")
                        || BypassCommand.isBypassing(cpLayer.getName())) {
                    clanManager.createClan(subargs[1], sender.getName());
                    HSClans.instance.getMessenger().broadcastToAll("commands.create.success", sender.getName(), subargs[1]);
                } else {
                    double hoursRequired = new BigDecimal(HSClans.instance.getConfig().getDouble("player.create-hours") - cpLayer.getHoursPlayedTotal())
                            .setScale(1, RoundingMode.HALF_UP).doubleValue();
                    HSClans.instance.getMessenger().message("commands.create.not-enough-hours", sender, String.valueOf(hoursRequired));
                }
            } else {
                HSClans.instance.getMessenger().message("commands.create.clan-exists", sender);
            }
        } else {
            HSClans.instance.getMessenger().message("commands.create.wrong-role", sender);
            return;
        }
    }
}
