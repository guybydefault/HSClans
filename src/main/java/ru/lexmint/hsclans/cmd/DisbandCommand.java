package ru.lexmint.hsclans.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hscore.cmd.AbstractCommand;

/**
 * Command which is used by admins to disband clans.
 */
class DisbandCommand extends AbstractCommand {


    public DisbandCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        /** TODO Tournament feature */
        if (HSClans.instance.getSettings().getBoolean("tournament.enable") && !sender.hasPermission("hsclans.command.bypass")) {
            HSClans.instance.getMessenger().message("messages.errors.tournament-deny", sender);
            return;
        }
        /* Tournament feature */

        ClanManager clanManager = HSClans.instance.getClanManager();
        Clan clan = clanManager.getClan(subargs[1]);
        if (clan != null) {
            HSClans.instance.getMessenger().broadcastToAll("commands.disband.success", sender.getName(), clan.getName());
            clanManager.removeClan(clan);
        } else {
            HSClans.instance.getMessenger().message("commands.disband.not-found", sender, subargs[1]);
        }
    }
}
