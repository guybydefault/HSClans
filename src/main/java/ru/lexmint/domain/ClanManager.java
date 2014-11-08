package ru.lexmint.domain;

/**
 * Interface which describes clan storage's behaviour.
 * It contains such methods as loading clans, saving them to
 * the permanent memory, etc.
 */
public interface ClanManager {
    /**
     * Loads clans from disk.
     */
    public void loadClans();

    /**
     * Gets CPLayer object of a player with given playerName.
     * @param playerName Name of the player.
     * @return CPLayer object of a player with given playerName.
     */
    public CPLayer getPlayer(String playerName);

    /**
     * Removes player from clan.
     * @param player CPlayer object of this player.
     */
    public void removePlayer(CPLayer player);

    /**
     * Adds player to clan.
     * @param player CPlayer object of this player.
     */
    public void addPlayer(CPLayer player);
}
