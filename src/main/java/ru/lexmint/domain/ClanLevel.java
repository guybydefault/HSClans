package ru.lexmint.domain;

import ru.lexmint.HSClans;

/**
 * Clan level describes clan's experience on server.
 */
public enum ClanLevel {
    LOW(HSClans.instance.getLangConfig().getString("clan.levels.low.name"), 0),
    MEDIUM(HSClans.instance.getLangConfig().getString("clan.levels.medium.name"), HSClans.instance.getSettings().getInt("clan.levels.medium.days")),
    HIGH(HSClans.instance.getLangConfig().getString("clan.levels.high.name"), HSClans.instance.getSettings().getInt("clan.levels.high.days"));

    /**
     * Localized name of clan level.
     */
    private String name;

    /**
     * Minimal number of days required for this clan level.
     */
    private int days;

    /**
     * Main constructor for creating clan level object.
     * @param name Localized name of clan level.
     * @param days Minimal number of days required for this clan level.
     */
    ClanLevel(String name, int days) {
        this.name = name;
        this.days = days;
    }

    /**
     *
     * @param days Days clan has been existed.
     * @return Clan level with maximum number of days limit which suits given number of days.
     */
    public static ClanLevel getClanLevelByDays(int days) {
        ClanLevel result = LOW;
        for (ClanLevel clanLevel : ClanLevel.values()) {
            if (clanLevel.getDays() > result.getDays()) {
                if (clanLevel.getDays() <= days) {
                    result = clanLevel;
                }
            }
        }
        return result;
    }

    /**
     *
     * @return Localized name of clan level.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Minimal number of days required for this clan level.
     */
    public int getDays() {
        return days;
    }
}
