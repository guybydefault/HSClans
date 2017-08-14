package ru.lexmint.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Deinvite player from clan.
 */
public class UninviteCommand extends HSCCommand {

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
    public UninviteCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        Clan clan = clanManager.getPlayer(sender.getName(), true).getClan();
        if (clan.pullInvitation(subargs[1])) {
            Player player = Bukkit.getPlayerExact(subargs[1]);
            if (player != null) {
                HSClans.instance.getMessenger().message("commands.uninvite.to-uninvited", player, sender.getName(), clan.getName());
            }
            HSClans.instance.getMessenger().message("commands.uninvite.success", sender, subargs[1]);
        } else {
            HSClans.instance.getMessenger().message("commands.uninvite.player-not-found", sender, subargs[1]);
        }
    }

}
