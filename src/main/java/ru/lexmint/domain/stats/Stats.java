package ru.lexmint.domain.stats;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class which deals with statistics information including total kills, total deaths and KillDeath rate.
 */
public abstract class Stats {
    /**
     * Number of kills made.
     */
    protected int kills;

    /**
     * Number of deaths.
     */
    protected int deaths;

    /**
     * KillDeath rate (PvP prestige).
     */
    protected double kdr;

    /**
     * Main constructor for Statistics.
     *
     * @param kills  Number of kills made.
     * @param deaths Number of deaths.
     */
    Stats(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    /**
     * Default constructor.
     */
    Stats() {
    }

    /**
     * @return Number of kills made.
     */
    public int getKills() {
        return kills;
    }

    /**
     * @return Number of deaths.
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * @return KillDeath rate (PvP prestige).
     */
    public abstract double getKDR();

    /**
     * @return Rounded value of KDR.
     */
    public double getKDRRounded() {
        return new BigDecimal(getKDR()).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }


}
