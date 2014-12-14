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
     * @param x X coordinate
     * @param z Z coordinate
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
     * Checks whether given coordinates are equal to the claim's coordinates (if locations are the same).
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return True if locations are equal. Otherwise, false.
     */
    public boolean isInClaim(int x, int z) {
        return claimLocation.getX() == x && claimLocation.getZ() == z;
    }

    /**
     * @param clan Clan which this claim is belonged to
     */
    void setClan(Clan clan) {
        this.clan = clan;
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
