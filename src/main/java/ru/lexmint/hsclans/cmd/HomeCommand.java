package ru.lexmint.hsclans.cmd;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.*;
import ru.lexmint.hsclans.integration.Essentials;

/**
 * Teleport to clan's home location.
 */
class HomeCommand extends AbstractClanPlayerCommand {


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
    public HomeCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);
        Clan clan = cpLayer.getClan();

        if (clan.hasHome()) {
            Player player = sender;
            Location homeLocation = clan.getHome();
            Claim claimTo = clanManager.getClaim(homeLocation.getChunk().getX(), homeLocation.getChunk().getZ(), homeLocation.getWorld());
            if (claimTo == null || !claimTo.canTeleportTo(cpLayer)) {
                HSClans.instance.getMessenger().message("commands.home.not-owned", player);
                return;
            }
            // TODO subargs[0] home! just home
            if (Essentials.handleTeleport(subargs[0], sender, homeLocation)) {
                return;
            }

            if (player.teleport(homeLocation, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                HSClans.instance.getMessenger().message("commands.home.success", sender);
            }
        } else {
            HSClans.instance.getMessenger().message("commands.home.not-set", sender);
        }
    }
}
