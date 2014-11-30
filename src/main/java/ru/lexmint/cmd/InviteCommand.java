package ru.lexmint.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * InviteCommand used for inviting players to the faction.
 */
public class InviteCommand extends BaseCommand {
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
    public InviteCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        Player player = HSClans.instance.getServer().getPlayer(subargs[1]);
        if (player != null) {
            ClanManager clanManager = HSClans.instance.getClanManager();
            Clan clan = clanManager.getPlayer(sender.getName(), true).getClan();
            if (clan.containsMember(player.getName())) {
                HSClans.instance.getMessenger().message("commands.invite.already-joined", sender, player.getName());
            } else if (clanManager.addInvite(player.getName(), clan.getName())) {
                CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);
                Clan playerClan = cpLayer.getClan();
                if (playerClan != null) {
                    HSClans.instance.getMessenger().message("commands.invite.success-busy", sender, player.getName(), playerClan.getName());
                    HSClans.instance.getMessenger().message("commands.invite.invitation-busy", player, sender.getName(), clan.getName());
                } else {
                    HSClans.instance.getMessenger().message("commands.invite.success", sender, player.getName());
                    HSClans.instance.getMessenger().message("commands.invite.invitation", player, sender.getName(), clan.getName());
                }
            } else {
                HSClans.instance.getMessenger().message("commands.invite.already-invited", sender, player.getName());
            }
        } else {
            HSClans.instance.getMessenger().message("commands.invite.player-not-found", sender);
        }
    }
}
