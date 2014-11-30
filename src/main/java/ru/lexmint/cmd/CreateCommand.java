package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Command for creating clan.
 */
public class CreateCommand extends BaseCommand {
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
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        if (cpLayer.getClanRole() == ClanRole.OUTLAW) {
            if (!clanManager.containsClan(subargs[1])) {
                clanManager.createClan(subargs[1], sender.getName());
                HSClans.instance.getMessenger().broadcastToAll("commands.create.success", sender.getName(), subargs[1]);
            } else {
               HSClans.instance.getMessenger().message("commands.create.clan-exists", sender);
            }
        } else {
            HSClans.instance.getMessenger().message("commands.create.wrong-role", sender);
            return;
        }
    }
}
