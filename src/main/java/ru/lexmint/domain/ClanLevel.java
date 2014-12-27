package ru.lexmint.domain;

/**
 * Clan level describes clan's experience on server.
 */
public enum ClanLevel {
    LOW(null, 0),
    MEDIUM(null, 0),
    HIGH(null, 0);

    private String name;

    private int days;

    ClanLevel(String name, int days) {
        this.name = name;
        this.days = days;
    }

    public static ClanLevel getClanLevelByDays(int days) {
        return null;
    }

    public String getName() {
        return name;
    }
}
