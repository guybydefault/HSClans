package ru.lexmint.domain;

import org.bukkit.World;
import ru.lexmint.cmd.BypassCommand;

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
     * Main constructor for creating a clam object.
     *
     * @param claimLocation
     * @param clan          Clan which this claim is belonged to
     */
    Claim(ClaimLocation claimLocation, Clan clan) {
        this.claimLocation = claimLocation;
        this.clan = clan;
    }

    /**
     * Main constructor for creating a clam object.
     *
     * @param x    X coordinate
     * @param z    Z coordinate
     * @param world World where this claim is
     * @param clan Clan which this claim is belonged to
     */
    Claim(int x, int z, World world, Clan clan) {
        claimLocation = new ClaimLocation(x, z, world);
        this.clan = clan;
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
        return (cpLayer.getClan() == getClan()
                || (cpLayer.hasClan() && cpLayer.getClan().isAlliedWith(getClan()))) || BypassCommand.isBypassing(cpLayer.getName());
    }

    /**
     * @param cpLayer Player for which we need to check right to teleport.
     * @return True if player has permission to teleport from this claim (if he is a member of the clan, owner of this
     * land or is in alliance with owner). Otherwise, false.
     */
    public boolean canTeleportFrom(CPLayer cpLayer) {
        return (cpLayer.getClan() == getClan() || (cpLayer.hasClan() && cpLayer.getClan().isAlliedWith(getClan()))) || BypassCommand.isBypassing(cpLayer.getName());
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
