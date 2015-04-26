package ru.lexmint.cmd;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.*;

/**
 * Sets home for the clan (teleport position).
 */
public class SethomeCommand extends HSCCommand {
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
    public SethomeCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        Player player = (Player) sender;
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
