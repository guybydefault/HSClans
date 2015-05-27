package ru.lexmint.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Claim;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Deals with clans combat.
 */
public class EntityListener implements Listener {
    private final Set<PotionEffectType> badPotionEffects;
    private final Set<ExplosionType> deniedExplosions;
    private final Set<ExplosionType> deniedExplosionsOffline;

    public EntityListener() {
        badPotionEffects = Utils.getPotionsSet("pvp.bad-potions");
        deniedExplosions = Utils.getExplosionsSet("claims.deny.explosion.always");
        deniedExplosionsOffline = Utils.getExplosionsSet("claims.deny.explosion.offline");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!canDamage(event.getDamager(), event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();
        Entity exploder = event.getEntity();

        ClanManager clanManager = HSClans.instance.getClanManager();
        Claim claim = clanManager.getClaim(location.getChunk().getX(), location.getChunk().getZ(), location.getWorld());
        /** Wilderness. **/
        if (claim == null) {
            return;
        }

        Clan clan = claim.getClan();
        // TODO: instanceof => entity.getType
        if (exploder instanceof Creeper && !canExplode(ExplosionType.CREEPER, clan)) {
            event.setCancelled(true);
        } else if (exploder instanceof Fireball && !canExplode(ExplosionType.FIREBALL, clan)) {
            event.setCancelled(true);
        } else if ((exploder instanceof WitherSkull || exploder instanceof Wither) && !canExplode(ExplosionType.WITHER, clan)) {
            event.setCancelled(true);
        } else if (exploder instanceof TNTPrimed && !canExplode(ExplosionType.TNT, clan)) {
            event.setCancelled(true);
        } else if (exploder instanceof ExplosiveMinecart && !canExplode(ExplosionType.MINECART, clan)) {
            event.setCancelled(true);
        } else if (exploder instanceof TNTPrimed || exploder instanceof ExplosiveMinecart) {
            handleTNTExploit(location);
        }
    }

    /**
     * Handles TNT water/lava exploit.
     *
     * @param location Location which tnt has been primed to.
     */
    private void handleTNTExploit(Location location) {
        /** TNT in water/lava doesn't normally destroy any surrounding blocks, which is usually desired behavior, but...
         * this change below provides workaround for water walling providing perfect protection,
         * and makes cheap (non-obsidian) TNT cannons require minor maintenance between shots. */
        Block center = location.getBlock();
        if (center.isLiquid()) {
            /** A single surrounding block in all 6 directions is broken if the material is weak enough. **/
            List<Block> targets = new ArrayList<Block>();
            targets.add(center.getRelative(0, 0, 1));
            targets.add(center.getRelative(0, 0, -1));
            targets.add(center.getRelative(0, 1, 0));
            targets.add(center.getRelative(0, -1, 0));
            targets.add(center.getRelative(1, 0, 0));
            targets.add(center.getRelative(-1, 0, 0));
            for (Block target : targets) {
                // TODO: Materials
                int id = target.getTypeId();
                // ignore air, bedrock, water, lava, obsidian, enchanting table, etc.... too bad we can't get a blast resistance value through Bukkit yet
                if (id != 0 && (id < 7 || id > 11) && id != 49 && id != 90 && id != 116 && id != 119 && id != 120 && id != 130) {
                    target.breakNaturally();
                }
            }
        }
    }

    /**
     * Check whether explosion of given explosion type can explode.
     *
     * @param explosionType Type of an explosion.
     * @param clan          Clan, which claim belongs to.
     * @return True if explosion can happen. Otherwise, false.
     */
    private boolean canExplode(ExplosionType explosionType, Clan clan) {
        if (deniedExplosions.contains(explosionType)) {
            return false;
        } else if (!clan.hasPlayersOnline() && deniedExplosionsOffline.contains(explosionType)) {

            if (HSClans.instance.getSettings().getBoolean("claims.explosion-exploit.handle")) {
                Long lastClanPlayed = clan.getLastPlayed();
                if (lastClanPlayed != null) {
                    long time = (System.currentTimeMillis() - lastClanPlayed) / 1000;
                    if (time <= HSClans.instance.getSettings().getInt("claims.explosion-exploit.time")) {
                        return true;
                    }
                }
            }

            return false;
        }
        return true;
    }

    /**
     * Mainly used for flaming arrows because they can cause damage even after damage event has been cancelled.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        if (!canDamage(event.getCombuster(), event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        /** Check potion for harmful effect **/
        boolean harmful = false;
        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (badPotionEffects.contains(effect.getType())) {
                harmful = true;
                break;
            }
        }
        if (!harmful) {
            return;
        }

        Entity thrower = event.getPotion().getShooter();
        if (thrower == null) {
            return;
        }

        /** Scanning through all affected entities to make sure they are all valid targets. **/
        Iterator<LivingEntity> it = event.getAffectedEntities().iterator();
        while (it.hasNext()) {
            LivingEntity target = it.next();
            if (!canDamage((Entity) thrower, target)) {
                /** Affected entity list doesn't accept modification (so no it.remove()), but this works fine. **/
                event.setIntensity(target, 0.0);
            }
        }
    }

    /**
     * Checks whether attacker can cause any damage to defender. It check their clans, relationship and other things
     * that must be kept in mind while fighting and shooting.
     *
     * @param attacker Entity which has tried to attack defender.
     * @param defender Entity which is going to suffer from attacker.
     * @return True if attacker can hurt defender. Otherwise, false.
     */
    public boolean canDamage(Entity attacker, Entity defender) {
        if (!(defender instanceof Player)) {
            return true;
        }
        if (attacker instanceof Projectile) {
            attacker = (Entity) ((Projectile) attacker).getShooter();
        }
        if (!(attacker instanceof Player)) {
            return true;
        }

        ClanManager clanManager = HSClans.instance.getClanManager();
        CPLayer cAttacker = clanManager.getPlayer(((Player) attacker).getName(), true);
        CPLayer cDefender = clanManager.getPlayer(((Player) defender).getName(), true);

        /** Friendly fire **/
        if (clanManager.areInTheSameClan(cAttacker, cDefender)) {
            HSClans.instance.getMessenger().message("messages.pvp.same-clan", (Player) attacker, cDefender.getName());
            return false;
        } else if (clanManager.areInAlliedClans(cAttacker, cDefender)) {
            HSClans.instance.getMessenger().message("messages.pvp.ally", (Player) attacker, cDefender.getName(), cDefender.getClan().getName());
            return false;
        } else {
            return true;
        }
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        Location loc = event.getBlock().getLocation();
        ClanManager clanManager = HSClans.instance.getClanManager();
        Claim claim = clanManager.getClaim(loc.getChunk().getX(), loc.getChunk().getZ(), loc.getWorld());

        /** Wilderness. */
        if (claim == null) {
            return;
        }

        if (entity instanceof Enderman) {
            if (HSClans.instance.getSettings().getBoolean("claims.deny.enderman-grief")) {
                event.setCancelled(true);
            }
        } else if (entity instanceof Wither) {
            if (HSClans.instance.getSettings().getBoolean("claims.deny.wither-grief")) {
                event.setCancelled(true);
            }
        }

    }

    /**
     * Unfortunately, Bukkit has not provided some explosion types, so, for better code I have introduced
     * my own ExplosionType enumeration. It it used during the check of explosion in listener and in config where denied
     * explosions are described.
     */
    public enum ExplosionType {
        CREEPER,
        FIREBALL,
        /**
         * Describes explosion from wither itself & wither skulls.
         */
        WITHER,
        TNT,
        /**
         * Explosive minecart, of course.
         */
        MINECART
    }
}
