package ru.lexmint.domain;

import ru.lexmint.HSClans;

/**
 * Clan leagues define the way players and clans fight with each other. For example, members of the lowest league
 * can't attack members of the highest one.
 */
public enum ClanLeague {
    /**
     * Lowest league
     */
    LOW(HSClans.instance.getLangConfig().getString("clan-leagues.low")),
    /**
     * Highest league
     */
    HIGH(HSClans.instance.getLangConfig().getString("clan-leagues.high"));

    /**
     * Localised name of the league.
     */
    private String name;

    /**
     * Main constructor for creating a league.
     * @param name Localised name of the league.
     */
    ClanLeague(String name) {
        this.name = name;
    }

    /**
     * Returns name of the league.
     * @return localised name of the league
     */
    public String getName() {
        return name;
    }


}
