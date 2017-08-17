package ru.lexmint.hsclans.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.Clan;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

/**
 * Deinvite player from clan.
 */
class UninviteCommand extends AbstractClanPlayerCommand {


    /**
     * Main constructor for creating a command.
     *
     * @param aliases
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param usage            String which contains information how to use this command.
     */
    public UninviteCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
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
