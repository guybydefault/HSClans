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
    LOW(HSClans.instance.getLangConfig().getString("clan.leagues.low.name"), HSClans.instance.getLangConfig().getString("clan.leagues.low.tag")),
    /**
     * Highest league
     */
    HIGH(HSClans.instance.getLangConfig().getString("clan.leagues.high.name"), HSClans.instance.getLangConfig().getString("clan.leagues.high.tag"));

    /**
     * Localised name of the league.
     */
    private String name;

    /**
     * Shorten name of the league which can be used in chat.
     */
    private String tag;

    /**
     * Main constructor for creating a league.
     *
     * @param name Localised name of the league.
     */
    ClanLeague(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    /**
     * Returns name of the league.
     *
     * @return localised name of the league
     */
    public String getName() {
        return name;
    }

    /**
     * @return Shorten name of the league which can be used in chat.
     */
    public String getTag() {
        return tag;
    }


}
