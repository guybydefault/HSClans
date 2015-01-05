package ru.lexmint.cmd;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.*;
import ru.lexmint.integration.Essentials;

/**
 * Teleport to clan's home location.
 */
public class HomeCommand extends BaseCommand {
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
    public HomeCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        Clan clan = cpLayer.getClan();

        if (clan.hasHome()) {
            Player player = (Player) sender;
            Location homeLocation = clan.getHome();
            Claim claimTo = clanManager.getClaim(homeLocation.getChunk().getX(), homeLocation.getChunk().getZ(), homeLocation.getWorld());
            if (claimTo == null || !claimTo.canTeleportTo(cpLayer)) {
                HSClans.instance.getMessenger().message("commands.home.not-owned", player);
                return;
            }
            // TODO subargs[0] home! just home
            if (Essentials.handleTeleport(subargs[0], (Player) sender, homeLocation)) {
                return;
            }

            if (player.teleport(homeLocation)) {
                HSClans.instance.getMessenger().message("commands.home.success", sender);
            }
        } else {
            HSClans.instance.getMessenger().message("commands.home.not-set", sender);
        }
    }
}
