package ru.lexmint.cmd;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.*;
import ru.lexmint.integration.WorldGuard;

import java.util.List;

public class ClaimCommand extends HSCCommand {
    /*
     * List of worlds where claiming is denied.
     */
    private final List<String> deniedWorlds = HSClans.instance.getSettings().getStringList("claims.denied-worlds");

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
    public ClaimCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        Player player = (Player) sender;
        int chunkZ = player.getLocation().getChunk().getZ();
        int chunkX = player.getLocation().getChunk().getX();
        World world = player.getLocation().getWorld();

        ClanRole minRole = ClanRole.USER;
        try {
            if (subargs.length > 1) {
                minRole = ClanRole.valueOf(subargs[1].toUpperCase());
            }
        } catch (IllegalArgumentException exc) {
            HSClans.instance.getMessenger().message("commands.claim.wrong-role", sender);
            return;
        }

        ClanManager clanManager = HSClans.instance.getClanManager();

        CPLayer cPlayer = clanManager.getPlayer(sender.getName(), true);
        Clan clan = cPlayer.getClan();
        Claim claim = clanManager.getClaim(chunkX, chunkZ, world);

        if (claim != null) {
            Clan owner = claim.getClan();
            if (owner == clan) {
                if (minRole != claim.getMinRole()) {
                    clanManager.changeClaimMinRole(claim, minRole);
                    HSClans.instance.getMessenger().message("commands.claim.role-changed-success", sender);
                } else {
                    HSClans.instance.getMessenger().message("commands.claim.already-own", sender);
                }
            } else if (clan.isAlliedWith(owner)) {
                HSClans.instance.getMessenger().message("commands.claim.ally", sender, owner.getName());
            } else if (owner.canHoldClaim()) {
                HSClans.instance.getMessenger().message("commands.claim.owner-can-hold", sender, owner.getName());
            } else if (clan.canClaim(1)) {
                clanManager.changeClaimClan(claim, clan, minRole);
                HSClans.instance.getMessenger().broadcastToClan("commands.claim.claim-captured-lost", owner, cPlayer.getName(),
                        clan.getName(), String.valueOf(chunkX), String.valueOf(chunkZ));
                HSClans.instance.getMessenger().broadcastToClan("commands.claim.claim-captured-win", clan, cPlayer.getClanRole().getName(),
                        cPlayer.getName(), owner.getName(), String.valueOf(chunkX), String.valueOf(chunkZ));
            } else {
                HSClans.instance.getMessenger().message("commands.claim.not-enough-power", sender, String.valueOf(clan.getClaimsNumber()), String.valueOf(clan.getPowerRounded()));
            }
        } else {
            if (WorldGuard.checkForRegionsInChunk(player.getLocation().getChunk())) {
                HSClans.instance.getMessenger().message("commands.claim.world-guard-region", sender);
            } else if (deniedWorlds.contains(world.getName())) {
                HSClans.instance.getMessenger().message("commands.claim.denied-world", sender);
            } else if (clan.canClaim(1)) {
                clanManager.addClaim(chunkX, chunkZ, world, clan, minRole);
                HSClans.instance.getMessenger().broadcastToClan("commands.claim.success", clan, cPlayer.getClanRole().getName(), cPlayer.getName(), String.valueOf(chunkX),
                        String.valueOf(chunkZ));
            } else {
                HSClans.instance.getMessenger().message("commands.claim.not-enough-power", sender,
                        String.valueOf(clan.getClaimsNumber()), String.valueOf(clan.getPowerRounded()));
            }
        }
    }
}
