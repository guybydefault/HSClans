package ru.lexmint.domain;

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
     * @param clan Clan which this claim is belonged to
     */
    Claim(int x, int z, Clan clan) {
        claimLocation = new ClaimLocation(x, z);
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
     * @return True if player has permission to teleport to this claim (if he is a member of the clan, owner of this land). Otherwise, false.
     */
    public boolean canTeleportTo(CPLayer cpLayer) {
        return cpLayer.getClan() == getClan();
    }

    /**
     *
     * @param cpLayer Player for which we need to check right to teleport.
     * @return True if player has permission to teleport from this claim (if he is a member of the clan, owner of this land). Otherwise, false.
     */
    public boolean canTeleportFrom(CPLayer cpLayer) {
        return cpLayer.getClan() == getClan();
    }

    class ClaimLocation {
        private int x;
        private int z;

        public ClaimLocation(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }
    }
}
