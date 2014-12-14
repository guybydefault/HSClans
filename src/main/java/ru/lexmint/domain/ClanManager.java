package ru.lexmint.domain;

import org.bukkit.entity.Player;
import ru.lexmint.HSClans;

import java.util.HashMap;
import java.util.Set;

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
     * List of all clan claims.
     */
    private Set<Claim> claims;

    /**
     * Object, which deals with MySQL interactions and queries.
     */
    private final StorageManager storageManager;

    public ClanManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Retrieves clans from MySQL storage (using StorageManager).
     */
    public void loadData() {
        Set<Clan> clanSet = storageManager.importClans();

        for (Clan clan : clanSet) {
            clansByName.put(clan.getName(), clan);
        }

        claims = storageManager.importClaims();

        for (Claim claim : claims) {
            claim.getClan().addClaim(claim);
        }

        for (Player player : HSClans.instance.getServer().getOnlinePlayers()) {
            storageManager.importClanPlayer(player.getName());
        }

    }

    /**
     * Adds new clan to storage. Also, adds new player (leader) to this clan.
     *
     * @param clanName   Name (tag) of the clan.
     * @param leaderName Name of clan's leader who will be added to the clan firstly.
     */
    public void createClan(String clanName, String leaderName) {
        CPLayer leader = getPlayer(leaderName, true);
        Clan clan = new Clan(clanName, leader.getClanLeague());
        clansByName.put(clanName, clan);
        storageManager.addClan(clan);

        leader.setClanRole(ClanRole.LEADER);
        addPlayerToClan(clan, leader);
    }

    /**
     * Adds player to clan.
     *
     * @param clan    Clan object.
     * @param cPlayer CPlayer object of player.
     */
    public void addPlayerToClan(Clan clan, CPLayer cPlayer) {
        clan.addPlayer(cPlayer.getName());
        cPlayer.setClan(clan);

        storageManager.updateClanPlayer(cPlayer);
        storageManager.updateClan(clan);
    }

    /**
     * Adds new player to database.
     *
     * @param playerName Name of the player.
     */
    public void createPlayer(String playerName) {
        CPLayer cpLayer = new CPLayer(playerName);
        clanPlayersByName.put(playerName, cpLayer);
        storageManager.addClanPlayer(cpLayer);
    }

    /**
     * Return clan player by name. If player with given name has not been found
     * in storage, it tries to import CPlayer object from MySQL database. If there is one
     * - it returns it, otherwise returns null;
     *
     * @param playerName Name of the player.
     * @param cache      Whether or not player should be cached in memory or not.
     * @return CPLayer if it was found. Otherwise, null.
     */
    public CPLayer getPlayer(String playerName, boolean cache) {
        CPLayer cpLayer = clanPlayersByName.get(playerName);
        if (cpLayer == null) {
            cpLayer = storageManager.importClanPlayer(playerName);
            if (cache && cpLayer != null) {
                clanPlayersByName.put(playerName, cpLayer);
            }
        }

        return cpLayer;
    }

    /**
     * Removes a player from clan if he is a clan member.
     * <p/>
     * If clan member is a leader of this clan, clan will be disbanded.
     *
     * @param playerName Name of the player.
     */
    public void removePlayerFromClan(String playerName) {
        CPLayer cpLayer = getPlayer(playerName, false);
        Clan clan = cpLayer.getClan();
        if (clan != null) {
            if (cpLayer.getClanRole() != ClanRole.LEADER) {
                clan.removePlayer(playerName);
                cpLayer.removeFromClan();
                storageManager.updateClan(clan);
            } else {
                cpLayer.removeFromClan();
                removeClan(clan);
            }
            storageManager.updateClanPlayer(cpLayer);
        }
    }

    public void removeClan(Clan clan) {
        for (String memberName : clan.getMembers()) {
            removePlayerFromClan(memberName);
        }

        for (Claim claim : clan.getClaims()) {
            claims.remove(claim);
            storageManager.removeClaim(claim);
        }

        clansByName.remove(clan.getName());
        storageManager.removeClan(clan);
    }

    /**
     * Returns whether clan exists or not.
     *
     * @param clanName Name (tag) of the clan.
     * @return True if clan contains in memory, otherwise false.
     */
    public boolean containsClan(String clanName) {
        return clansByName.containsKey(clanName);
    }


    /**
     * Returns clan by name. May return null if clan has not been found in storage.
     *
     * @param clanName Name of the clan.
     * @return Clan object.
     */
    public Clan getClan(String clanName) {
        return clansByName.get(clanName);
    }

    /**
     * Removes CPLayer object from memory (for perfomance).
     *
     * @param playerName Name of the player.
     * @return CPlayer object which has been removed.
     */
    public CPLayer clearPlayerCache(String playerName) {
        return clanPlayersByName.remove(playerName);
    }

    /**
     * Makes an update (synchronise) of player's info to the database.
     *
     * @param cpLayer player which is needed to be updated
     */
    public void updatePlayer(CPLayer cpLayer) {
        storageManager.updateClanPlayer(cpLayer);
    }

    /**
     * Makes an update (synchronise) of clan's info to the database.
     *
     * @param clan clan which is needed to be updated
     */
    public void updateClan(Clan clan) {
        storageManager.updateClan(clan);
    }

    /**
     * Adds claim to storage and cache.
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return Claim which has been created and storaged
     */
    public Claim addClaim(int x, int z, Clan clan) {
        Claim claim = new Claim(x, z, clan);

        claims.add(claim);
        clan.addClaim(claim);

        updateClan(clan);
        storageManager.addClaim(claim);
        return claim;
    }

    /**
     * @param claim Claim which owner needs to be changed
     * @param clan  Clan which given claim is supposed to be belonged to
     */
    public void changeClaimClan(Claim claim, Clan clan) {
        claim.getClan().removeClaim(claim);
        updateClan(claim.getClan());

        claim.setClan(clan);
        clan.addClaim(claim);
        updateClan(clan);

        storageManager.updateClaimClan(claim);
    }

    /**
     * @param x X coordinate
     * @param z Z coordinate
     * @return Claim object
     */
    // TODO Rewrite using hashCode, equals
    public Claim getClaim(int x, int z) {
        for (Claim claim : claims) {
            if (claim.getClaimLocation().getX() == x && claim.getClaimLocation().getZ() == z) {
                return claim;
            }
        }
        return null;
    }

    /**
     * Removes claim from cache and storage.
     *
     * @param x X coordinate
     * @param z Z coordinate
     */
    public void removeClaim(int x, int z) {
        Claim claim = getClaim(x, z);
        claim.getClan().removeClaim(claim);
        updateClan(claim.getClan());
        claims.remove(claim);
        storageManager.removeClaim(claim);
    }
}
