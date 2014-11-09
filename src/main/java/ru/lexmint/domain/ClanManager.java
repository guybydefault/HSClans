package ru.lexmint.domain;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Class which controls all clans of the server, loads them, etc.
 * It stores clans using MySQL.
 */
public class ClanManager {
    /**
     * Map of all clans existing on the server by their name.
     */
    private final HashMap<String, Clan> clansByName = new HashMap<>();

    /**
     * Map of all clan players existing on the server by their name.
     */
    private final HashMap<String, CPLayer> clanPlayersByName = new HashMap<>();

    /**
     * Object, which deals with MySQL interactions and queries.
     */
    private final StorageManager storageManager;

    public ClanManager(StorageManager storageManager) {
        this.storageManager = storageManager;
        loadClans();
    }

    /**
     * Retrieves clans from MySQL storage (using StorageManager).
     */
    private void loadClans() {
        List<Clan> clanList = storageManager.importClans();
        for (Clan clan : clanList) {
            for (CPLayer clanMember : clan.getMembers()) {
                clanPlayersByName.put(clanMember.getName(), clanMember);
            }
            clansByName.put(clan.getName(), clan);
        }
    }

    /**
     * Return clan player by name. If player with given name has not been found
     * in storage, it tries to import CPlayer object from MySQL database. If there is one
     * - it returns it, otherwise returns null;
     * @param playerName Name of the player.
     * @return CPLayer if it was found. Otherwise, null.
     */
    public CPLayer getPlayer(String playerName) {
        CPLayer cpLayer = clanPlayersByName.get(playerName);
        if (cpLayer == null) {
            cpLayer = storageManager.importClanPlayer(playerName);
        }
        return cpLayer;
    }

    /**
     * Returns clan by name. May return null if clan has not been found in storage.
     * @param clanName Name of the clan.
     * @return Clan object.
     */
    public Clan getClan(String clanName) {
        return clansByName.get(clanName);
    }


}
