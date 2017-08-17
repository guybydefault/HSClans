package ru.lexmint.hsclans.cmd;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Claim;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.ClanRole;

/**
 * Command which unclaims chunk or many chunks of the clan.
 */
class UnclaimCommand extends AbstractClanPlayerCommand {


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
    public UnclaimCommand(String[] aliases, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(aliases, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(Player sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cpLayer = clanManager.getPlayer(sender.getName(), true);

        if (subargs.length >= 2 && subargs[1].equalsIgnoreCase("all")) {
            int unclaimedLand = clanManager.removeAllClaims(cpLayer.getClan());
            if (unclaimedLand != 0) {
                HSClans.instance.getMessenger().broadcastToClan("commands.unclaim.success-all", cpLayer.getClan(),
                        cpLayer.getClanRole().getName(), cpLayer.getName(), String.valueOf(unclaimedLand));
            } else {
                HSClans.instance.getMessenger().message("commands.unclaim.no-claims", sender);
            }
        } else {
            Player player = sender;

            int chunkX = player.getLocation().getChunk().getX();
            int chunkZ = player.getLocation().getChunk().getZ();

            Claim claim = clanManager.getClaim(chunkX, chunkZ, player.getLocation().getWorld());

            if (claim == null) {
                HSClans.instance.getMessenger().message("commands.unclaim.wilderness", sender);
            } else if (claim.getClan() != cpLayer.getClan()) {
                HSClans.instance.getMessenger().message("commands.unclaim.not-owned", sender, claim.getClan().getName());
            } else {
                clanManager.removeClaim(claim);
                HSClans.instance.getMessenger().broadcastToClan("commands.unclaim.success", cpLayer.getClan(),
                        cpLayer.getClanRole().getName(), cpLayer.getName(), String.valueOf(chunkX), String.valueOf(chunkZ));
            }
        }
    }
}
