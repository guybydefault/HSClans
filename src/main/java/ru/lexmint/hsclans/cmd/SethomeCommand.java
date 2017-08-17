package ru.lexmint.hsclans.cmd;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.*;

/**
 * Sets home for the clan (teleport position).
 */
class SethomeCommand extends AbstractClanPlayerCommand {


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
    public SethomeCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        Player player = sender;
        Location location = player.getLocation();

        ClanManager clanManager = HSClans.instance.getClanManager();
        Chunk playerChunk = player.getLocation().getChunk();
        Claim currentClaim = clanManager.getClaim(playerChunk.getX(), playerChunk.getZ(), location.getWorld());

        if (currentClaim == null) {
            HSClans.instance.getMessenger().message("commands.sethome.wilderness", player);
            return;
        }

        CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);

        if (currentClaim.getClan() != cpLayer.getClan()) {
            HSClans.instance.getMessenger().message("commands.sethome.not-owned", player, currentClaim.getClan().getName());
            return;
        }

        if (location.getY() < HSClans.instance.getSettings().getDouble("clan.home.min-height")) {
            HSClans.instance.getMessenger().message("commands.sethome.low-height", sender);
            return;
        }

        Clan clan = cpLayer.getClan();
        clan.setHome(location);
        clanManager.updateClan(clan);
        HSClans.instance.getMessenger().broadcastToClan("commands.sethome.changed", clan, cpLayer.getClanRole().getName(), cpLayer.getName());
    }
}
