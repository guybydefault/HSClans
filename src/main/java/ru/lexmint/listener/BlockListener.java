package ru.lexmint.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import ru.lexmint.HSClans;
import ru.lexmint.cmd.BypassCommand;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Claim;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

/**
 * Class which protects clan's claims.
 */
public class BlockListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.canBuild()) {
            return;
        }
        /** Special case for Flint & Steel, which should only be prevented by DenyUsage list **/
        if (event.getBlockPlaced().getType() == Material.FIRE) {
            return;
        }

        if (!canChangeBlock(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!canChangeBlock(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getInstaBreak() && !canChangeBlock(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        /** Target end-of-the-line empty (air) block which is being pushed into,
         * including if piston itself would extend into air. **/
        Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

        /** If potentially pushing into air/water/lava in another territory, we need to check it out **/
        if ((targetBlock.isEmpty() || targetBlock.isLiquid())
                && !canPistonMoveBlock(event.getBlock().getLocation(), targetBlock.getLocation())) {
            event.setCancelled(true);
            return;
        }

		/*
         * Note that I originally was testing the territory of each affected block, but since I found that pistons can only push
		 * up to 12 blocks and the width of any territory is 16 blocks, it should be safe (and much more lightweight) to test
		 * only the final target block as done above.
		 */
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        /** if not a sticky piston, retraction should be fine **/
        if (!event.isSticky()) {
            return;
        }

        Location target = event.getRetractLocation();

        /** If potentially retracted block is just air/water/lava, no worries **/
        if (target.getBlock().isEmpty() || target.getBlock().isLiquid()) {
            return;
        }

        if (!canPistonMoveBlock(event.getBlock().getLocation(), target)) {
            event.setCancelled(true);
            return;
        }
    }

    private boolean canPistonMoveBlock(Location from, Location to) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        Claim claimFrom = clanManager.getClaim(from.getChunk().getX(), from.getChunk().getZ(), from.getWorld());
        Claim claimTo = clanManager.getClaim(to.getChunk().getX(), to.getChunk().getZ(), to.getWorld());
        return claimTo == null || claimFrom.getClan() == claimTo.getClan();
    }

    private boolean canChangeBlock(Player player, Location location) {
        if (BypassCommand.isBypassing(player.getName())) {
            return true;
        }

        ClanManager clanManager = HSClans.instance.getClanManager();
        Claim claim = clanManager.getClaim(location.getChunk().getX(), location.getChunk().getZ(), location.getWorld());
        if (claim == null) {
            return true;
        } else {
            CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);
            if (claim.getClan() == cpLayer.getClan()) {
                if (cpLayer.getClanRole().getLevel() <= ClanRole.NEWBIE.getLevel()) {
                    HSClans.instance.getMessenger().message("messages.build.not-enough-perms", player, cpLayer.getClanRole().getName());
                    return false;
                } else {
                    return true;
                }
            } else {
                HSClans.instance.getMessenger().message("messages.build.not-owned", player, claim.getClan().getName());
                return false;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        Entity breaker = event.getRemover();
        if (!(breaker instanceof Player)) {
            return;
        }

        if (!canChangeBlock((Player) breaker, event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPaintingPlace(HangingPlaceEvent event) {
        if (!canChangeBlock(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
