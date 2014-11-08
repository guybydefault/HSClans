package ru.lexmint.domain;

/**
 * Class, describing a player who is or was in a clan.
 */
public class CPLayer {
    /**
     * In-game name of a player.
     */
    private String name;
    /**
     * Clan of a player.
     */
    private Clan clan;

    /**
     * Return in-game name of a player.
     * @return String containing in-game name of a player.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the clan in which player exists.
     * @return Clan object (clan of this player).
     */
    public Clan getClan() {
        return clan;
    }
}
