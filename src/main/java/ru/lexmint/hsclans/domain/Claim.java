package ru.lexmint.hsclans.domain;

import org.bukkit.World;
import ru.lexmint.hsclans.HSClans;

/**
 * Class which describes claimed location of a clan (chunk).
 */
public class Claim {
    /**
     * Location of a claimed chunk.
     */
    private ClaimLocation claimLocation;
    /**
     * Clan which this claim is belonged to.
     */
    private Clan clan;

    /**
     * Min role which is required to interact with the land.
     */
    private ClanRole minRole;

    /**
     * Main constructor for creating a clam object.
     *
     * @param claimLocation
     * @param clan          Clan which this claim is belonged to
     */
    Claim(ClaimLocation claimLocation, Clan clan, ClanRole minRole) {
        this.claimLocation = claimLocation;
        this.clan = clan;
        this.minRole = minRole;
    }

    /**
     * Main constructor for creating a clam object.
     *
     * @param x     X coordinate
     * @param z     Z coordinate
     * @param world World where this claim is
     * @param clan  Clan which this claim is belonged to
     */
    Claim(int x, int z, World world, Clan clan, ClanRole minRole) {
        claimLocation = new ClaimLocation(x, z, world);
        this.clan = clan;
        this.minRole = minRole;
    }

    /**
     * @return chunk location of a claim
     */
    public ClaimLocation getClaimLocation() {
        return claimLocation;
    }

    /**
     * @return Clan which this claim is belonged to
     */
    public Clan getClan() {
        return clan;
    }

    void setMinRole(ClanRole minRole) {
        this.minRole = minRole;
    }

    /**
     * @return Min role which is required to interact with the land.
     */
    public ClanRole getMinRole() {
        return minRole;
    }


    /**
     * @param clan Clan which this claim is belonged to
     */
    void setClan(Clan clan) {
        this.clan = clan;
    }

    /**
     * @param cpLayer Player for which we need to check right to teleport.
     * @return True if player has permission to teleport to this claim (if he is a member of the clan, owner of
     * this land or is in alliance with owner). Otherwise, false.
     */
    public boolean canTeleportTo(CPLayer cpLayer) {
        return cpLayer.getClan() == getClan()
                || (cpLayer.hasClan() && cpLayer.getClan().isAlliedWith(getClan()));
    }

    /**
     * @param cpLayer Player for which we need to check right to teleport.
     * @return True if player has permission to teleport from this claim (if he is a member of the clan, owner of this
     * land or is in alliance with owner). Otherwise, false.
     */
    public boolean canTeleportFrom(CPLayer cpLayer) {
        if (getClan().hasPlayersOnline()) {
            if (HSClans.instance.getSettings().getBoolean("claims.teleport-from.online")) {
                return true;
            }
        } else {
            if (HSClans.instance.getSettings().getBoolean("claims.teleport-from.offline")) {
                return true;
            }
        }

        if (cpLayer.getClan() == getClan() || (cpLayer.hasClan() && cpLayer.getClan().isAlliedWith(getClan()))) {
            return true;
        }

        return false;

    }

    class ClaimLocation {
        private int x;
        private int z;
        private World world;

        public ClaimLocation(int x, int z, World world) {
            this.x = x;
            this.z = z;
            this.world = world;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        public World getWorld() {
            return world;
        }
    }
}
