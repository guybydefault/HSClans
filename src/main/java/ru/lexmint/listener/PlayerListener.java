package ru.lexmint.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.lexmint.HSClans;
import ru.lexmint.domain.*;
import ru.lexmint.utils.Utils;

import java.util.Set;


/**
 * Deals with player respawn at faction's land and other things.
 */
public class PlayerListener implements Listener {
    private final Set<Material> deniedUsageOffline;
    private final Set<Material> deniedUsage;
    private final Set<Material> deniedUsageNewbie;
    private final Set<Material> deniedUsageAllie;

    private final Set<Material> deniedInteractOffline;
    private final Set<Material> deniedInteract;
    private final Set<Material> deniedInteractNewbie;
    private final Set<Material> deniedInteractAllie;

    // TODO: Interaction spam
    // for handling people who repeatedly spam attempts to open a door (or similar) in another faction's territory
//    private Map<String, InteractAttemptSpam> interactSpammers = new HashMap<String, InteractAttemptSpam>();


    public PlayerListener() {
        deniedUsage = Utils.getMaterialsSet("claims.deny.usage.always");
        deniedUsageOffline = Utils.getMaterialsSet("claims.deny.usage.offline");
        deniedUsageNewbie = Utils.getMaterialsSet("claims.deny.usage.newbie");
        deniedUsageAllie = Utils.getMaterialsSet("claims.deny.usage.allie");
        deniedInteract = Utils.getMaterialsSet("claims.deny.interact.always");
        deniedInteractOffline = Utils.getMaterialsSet("claims.deny.interact.offline");
        deniedInteractNewbie = Utils.getMaterialsSet("claims.deny.interact.newbie");
        deniedInteractAllie = Utils.getMaterialsSet("claims.deny.interact.allie");

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (HSClans.instance.getSettings().getBoolean("clan.home.respawn")) {
            ClanManager clanManager = HSClans.instance.getClanManager();
            CPLayer cpLayer = clanManager.getPlayer(event.getPlayer().getName(), true);
            if (cpLayer.hasClan()) {
                Clan clan = cpLayer.getClan();
                if (clan.hasHome()) {
                    HSClans.instance.getDebug().info("Respawn location for player " + event.getPlayer().getName() + " changed to clan's home");
                    event.setRespawnLocation(clan.getHome());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        /** Only need to check right-clicks and physical as of MC 1.4+; good performance boost. **/
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL
                || event.getAction() == Action.RIGHT_CLICK_AIR) {
            return;
        }

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (!canPlayerUseBlock(player, block.getLocation(), block.getType())) {
            event.setCancelled(true);
            // TODO: Interaction spam
//            if (Conf.handleExploitInteractionSpam) {
//                String name = player.getName();
//                InteractAttemptSpam attempt = interactSpammers.get(name);
//                if (attempt == null) {
//                    attempt = new InteractAttemptSpam();
//                    interactSpammers.put(name, attempt);
//                }
//                int count = attempt.increment();
//                if (count >= 10) {
//                    FPlayer me = FPlayers.i.get(name);
//                    me.msg("<b>Ouch, that is starting to hurt. You should give it a rest.");
//                    player.damage(NumberConversions.floor((double) count / 10));
//                }
//            }
            return;
        }

        /** Below we need just right click actions. **/
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!canPlayerUseItem(player, block.getLocation(), event.getMaterial())) {
            event.setCancelled(true);
            return;
        }
    }

    /**
     * For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks
     * away isn't detected), but these separate bucket events below always fire without fail. *
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!canPlayerUseItem(event.getPlayer(), event.getBlockClicked().getLocation(), event.getBucket())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!canPlayerUseItem(event.getPlayer(), event.getBlockClicked().getLocation(), event.getBucket())) {
            event.setCancelled(true);
            return;
        }
    }

    // TODO: Interaction spam
//    private static class InteractAttemptSpam {
//        private int attempts = 0;
//        private long lastAttempt = System.currentTimeMillis();
//
//        // returns the current attempt count
//        public int increment() {
//            long Now = System.currentTimeMillis();
//            if (Now > lastAttempt + 2000) {
//                attempts = 1;
//            } else {
//                attempts++;
//            }
//            lastAttempt = Now;
//            return attempts;
//        }
//    }

    /**
     * Checks whether player can interact with item of given material at given location.
     *
     * @param player   Player who has interacted with some item.
     * @param location Location where player has interacted with some item.
     * @param material Material of the item player interacted with.
     * @return True if player has permission to interact with it there or false, otherwise.
     */
    private boolean canPlayerUseBlock(Player player, Location location, Material material) {
        HSClans.instance.getDebug().info("Can " + player.getName() + " use block of type " + material + "?");
        ClanManager clanManager = HSClans.instance.getClanManager();
        Claim claim = clanManager.getClaim(location.getChunk().getX(), location.getChunk().getZ());

        /** Wilderness **/
        if (claim == null) {
            return true;
        }

        CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);
        Clan owner = claim.getClan();
        if (owner != cpLayer.getClan()) {
            if (owner.isAlliedWith(cpLayer.getClan())) {
                if (deniedInteractAllie.contains(material)) {
                    HSClans.instance.getMessenger().message("messages.interact.deny-allie", player, owner.getName());
                    return false;
                }
            } else {
                if (deniedInteract.contains(material)) {
                    HSClans.instance.getMessenger().message("messages.interact.deny", player, owner.getName());
                    return false;
                }
                if (!owner.hasPlayersOnline()) {
                    if (deniedInteractOffline.contains(material)) {
                        HSClans.instance.getMessenger().message("messages.interact.deny-offline", player, owner.getName());
                        return false;
                    }
                }
            }
        } else {
            if (cpLayer.getClanRole() == ClanRole.NEWBIE) {
                if (deniedInteractNewbie.contains(material)) {
                    HSClans.instance.getMessenger().message("messages.interact.deny-newbie", player, owner.getName());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks whether player can use item of given material at given location.
     *
     * @param player   Player who has used some item.
     * @param location Location where player has used some item.
     * @param material Material of the item used.
     * @return True if player has permission to use it there or false, otherwise.
     */
    private boolean canPlayerUseItem(Player player, Location location, Material material) {
        HSClans.instance.getDebug().info("Can " + player.getName() + " use item of type " + material + "?");

        ClanManager clanManager = HSClans.instance.getClanManager();
        Claim claim = clanManager.getClaim(location.getChunk().getX(), location.getChunk().getZ());

        /** Wilderness **/
        if (claim == null) {
            return true;
        }

        CPLayer cpLayer = clanManager.getPlayer(player.getName(), true);

        if (claim.getClan() == cpLayer.getClan()) {
            if (cpLayer.getClanRole() == ClanRole.NEWBIE) {
                if (deniedUsageNewbie.contains(material)) {
                    HSClans.instance.getMessenger().message("messages.use.deny-newbie", player, cpLayer.getClanRole().getName());
                    return false;
                }
            } else {
                return true;
            }
        } else {
            Clan owner = claim.getClan();
            if (owner.isAlliedWith(cpLayer.getClan())) {
                if (deniedUsageAllie.contains(material)) {
                    HSClans.instance.getMessenger().message("messages.use.deny-allie", player, owner.getName());
                    return false;
                }
            } else {
                if (deniedUsage.contains(material)) {
                    HSClans.instance.getMessenger().message("messages.use.deny", player, owner.getName());
                    return false;
                }
                if (!owner.hasPlayersOnline()) {
                    if (deniedUsageOffline.contains(material)) {
                        HSClans.instance.getMessenger().message("messages.use.deny-offline", player, owner.getName());
                        return false;
                    }
                }
            }
        }
        return true;
    }


}
