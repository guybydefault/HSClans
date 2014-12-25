package ru.lexmint.domain.stats;

import java.util.Set;

/**
 * Hold statistics for a clan.
 */
public class ClanStats extends Stats {

    /**
     * Statistics of all players who are members of this clan.
     */
    private Set<PlayerStats> playersStats;

    /**
     * Main constructor for Statistics.
     *
     * @param playersStats Statistics of all players who are members of this clan.
     */
    public ClanStats(Set<PlayerStats> playersStats) {
        super();
        this.playersStats = playersStats;
    }

    /**
     * Change players stats.
     * @param playersStats Statistics of all players who are members of this clan.
     */
    public void reloadStats(Set<PlayerStats> playersStats) {
        this.playersStats = playersStats;
    }

    /**
     * Updates statistics according to all players stats (pvp stats of all players in a clan).
     */
    public void updateStats() {
        kills = 0;
        deaths = 0;
        kdr = 0;
        for (PlayerStats stats : playersStats) {
            kills += stats.getKills();
            deaths += stats.getDeaths();
            kdr += stats.getKDR();
        }
        kdr /= playersStats.size();
    }

    @Override
    public double getKDR() {
        updateStats();
        return kdr;
    }

    @Override
    public int getKills() {
        updateStats();
        return kills;
    }

    @Override
    public int getDeaths() {
        updateStats();
        return deaths;
    }


}
