package ru.lexmint.domain.stats;

/**
 * Holds statistics for a player.
 */
public class PlayerStats extends Stats {

    /**
     * Main constructor for Statistics.
     *
     * @param kills  Number of kills made.
     * @param deaths Number of deaths.
     */
    public PlayerStats(int kills, int deaths) {
        super(kills, deaths);

    }

    public void incrementKills() {
        kills++;
    }

    public void incrementDeaths() {
        deaths++;
    }

    /**
     * Sets kills, deaths and KDR to zero.
     */
    public void reset() {
        kills = 0;
        deaths = 0;
        kdr = 0;
    }

    /**
     * Updates KillDeathRate according to current number of kills and deaths.
     */
    private void updateKDR() {
        if (deaths != 0) {
            kdr = (double) kills / deaths;
        } else {
            kdr = kills;
        }
    }

    @Override
    public double getKDR() {
        updateKDR();
        return kdr;
    }
}
