package ru.lexmint.domain;

/**
 * Class, describing a player who is or was in a clan.
 */
public class CPLayer {
    /**
     * In-game name of a player.
     */
    private final String name;

    /**
     * Clan of a player. May be null.
     */
    private Clan clan;

    /**
     * Role of a player in a clan.
     */
    private ClanRole clanRole;

    /**
     * Constructor for player without clan.
     *
     * @param name Name of the player.
     */
    public CPLayer(String name) {
        this.name = name;
        clanRole = ClanRole.OUTLAW;
    }

    /**
     * Standard constructor for creating a CPLayer.
     *
     * @param name     Name of the player.
     * @param clan     Player's clan.
     * @param clanRole Player's role in the given clan.
     */
    public CPLayer(String name, Clan clan, ClanRole clanRole) {
        this.name = name;
        this.clan = clan;
        this.clanRole = clanRole;
    }

    /**
     * Return in-game name of a player.
     *
     * @return String containing in-game name of a player.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the clan in which player exists.
     *
     * @return Clan object (clan of this player).
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * Sets clan for this CPLayer.
     *
     * @param clan Player's clan.
     */
    void setClan(Clan clan) {
        this.clan = clan;
    }

    /**
     * Return player's role in a clan.
     *
     * @return ClanRole object with player's role in a clan.
     */
    public ClanRole getClanRole() {
        return clanRole;
    }

    /**
     * Removes data of clan for this player.
     */
    public void removeFromClan() {
        clanRole = ClanRole.OUTLAW;
        clan = null;
    }

    /**
     * Sets player's role in clan.
     * @param clanRole Player's role in the clan.
     */
     void setClanRole(ClanRole clanRole) {
        this.clanRole = clanRole;
    }

    /**
     * Returns player's status in a clan.
     * @return True if player is in clan. Otherwise, false.
     */
    public boolean isInClan() { return (clanRole == ClanRole.OUTLAW); }
}
