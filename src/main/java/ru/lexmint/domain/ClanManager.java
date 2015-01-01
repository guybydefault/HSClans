package ru.lexmint.domain;

import ru.lexmint.HSClans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class which controls all clans of the server, loads them, etc.
 * It stores clans using MySQL.
 */
public class ClanManager {
    /**
     * Map of all clan players existing on the server by their name.
     */
    private final HashMap<String, CPLayer> clanPlayersByName = new HashMap<>();
    /**
     * Object, which deals with MySQL interactions and queries.
     */
    private final StorageManager storageManager;
    /**
     * Map of all clans existing on the server by their name.
     */
    private Map<String, Clan> clansByName = new HashMap<>();
    /**
     * List of all clan claims.
     */
    private Set<Claim> claims;

    public ClanManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Retrieves clans from MySQL storage (using StorageManager).
     */
    public void loadData() {
        clansByName = storageManager.importClans(true);
        claims = storageManager.importClaims();

        /**
         * All imported claims are added to clans' local cache.
         */
        for (Claim claim : claims) {
            claim.getClan().addClaim(claim);
        }

        /**
         * Adds players in clans to player cache for performance needs if it's configured in config.
         */
        if (HSClans.instance.getSettings().getBoolean("performance.cache-clan-players")) {
            for (Clan clan : clansByName.values()) {
                for (String clanMember : clan.getMembers()) {
                    getPlayer(clanMember, true);
                }
            }
        }

    }

    /**
     * Puts clan player to cache (HashMap which contains players and returns it by their name) using
     * player's name in lower case as a key.
     *
     * @param cpLayer CPLayer which will be cached.
     */
    private void addCPlayerToCache(CPLayer cpLayer) {
        clanPlayersByName.put(cpLayer.getName().toLowerCase(), cpLayer);
    }

    /**
     * Adds new clan to storage. Also, adds new player (leader) to this clan.
     *
     * @param clanName   Name (tag) of the clan.
     * @param leaderName Name of clan's leader who will be added to the clan firstly.
     */
    public void createClan(String clanName, String leaderName) {
        CPLayer leader = getPlayer(leaderName, true);
        Clan clan = new Clan(clanName, System.currentTimeMillis());
        clansByName.put(clanName.toLowerCase(), clan);
        storageManager.addClan(clan);

        addPlayerToClan(clan, leader, ClanRole.LEADER);
    }

    /**
     * Adds player to clan.
     *
     * @param clan    Clan object.
     * @param cPlayer CPlayer object of player.
     */
    public void addPlayerToClan(Clan clan, CPLayer cPlayer, ClanRole clanRole) {
        clan.addPlayer(cPlayer.getName());
        cPlayer.setClan(clan);
        cPlayer.setClanRole(clanRole);

        updatePlayer(cPlayer);
        updateClan(clan);

    }

    /**
     * Adds new player to database.
     *
     * @param playerName Name of the player.
     * @return Created CPlayer object.
     */
    public CPLayer createPlayer(String playerName) {
        HSClans.instance.getDebug().info("Creating player " + playerName);
        CPLayer cpLayer = new CPLayer(playerName, HSClans.instance.getSettings().getDouble("power.start-value"));
        addCPlayerToCache(cpLayer);
        storageManager.addClanPlayer(cpLayer);
        return cpLayer;
    }

    /**
     * Return clan player by name (case insensitive). If player with given name has not been found
     * in storage, it tries to import CPlayer object from MySQL database. If there is one
     * - it returns it, otherwise returns null.
     *
     * @param playerName Name of the player (no matter in which case, method is case insensitive).
     * @param cache      Whether or not player should be cached in memory or not. Notice that if setting cache-all in
     *                   plugin configuration is set to true, player will always be cached in memory.
     * @return CPLayer if it was found. Otherwise, null.
     */
    public CPLayer getPlayer(String playerName, boolean cache) {
        CPLayer cpLayer = clanPlayersByName.get(playerName.toLowerCase());
        if (cpLayer == null) {
            HSClans.instance.getDebug().info("Importing player " + playerName);
            cpLayer = storageManager.importClanPlayer(playerName);
            if ((HSClans.instance.getSettings().getBoolean("performance.cache-on-request") || cache) && cpLayer != null) {
                addCPlayerToCache(cpLayer);
            }
            HSClans.instance.getDebug().info("Imported player " + cpLayer);
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
        removePlayerFromClan(cpLayer);
    }

    /**
     * Removes a player from clan if he is a clan member.
     * <p/>
     *
     * @param cpLayer CPLayer object describing this player.
     */
    public void removePlayerFromClan(CPLayer cpLayer) {
        Clan clan = cpLayer.getClan();

        clan.removePlayer(cpLayer.getName());
        cpLayer.removeFromClan();
        updateClan(clan);

        updatePlayer(cpLayer);

    }

    /**
     * Removes clan completely, removes player from this clan, disclaims all clan's claims.
     *
     * @param clan Clan which will be removed.
     */
    public void removeClan(Clan clan) {
        for (String memberName : clan.getMembers()) {
            removePlayerFromClan(memberName);
        }

        for (Claim claim : clan.getClaims()) {
            claims.remove(claim);
            storageManager.removeClaim(claim);
        }

        clansByName.remove(clan.getName().toLowerCase());
        storageManager.removeClan(clan);
    }

    /**
     * Disclaims all clan claims.
     *
     * @param clan Clan which claim will be disclaimed.
     * @return Number of claims disclaimed.
     */
    public int removeAllClaims(Clan clan) {
        int disclaims = 0;
        for (Claim claim : clan.getClaims()) {
            clan.removeClaim(claim);
            claims.remove(claim);
            storageManager.removeClaim(claim);
            disclaims++;
        }
        updateClan(clan);
        return disclaims;
    }

    /**
     * Returns whether clan exists or not.
     *
     * @param clanName Name (tag) of the clan.
     * @return True if clan contains in memory, otherwise false.
     */
    public boolean containsClan(String clanName) {
        return clansByName.containsKey(clanName.toLowerCase());
    }


    /**
     * Returns clan by name. May return null if clan has not been found in storage.
     *
     * @param clanName Name of the clan.
     * @return Clan object.
     */
    public Clan getClan(String clanName) {
        return clansByName.get(clanName.toLowerCase());

    }

    /**
     * Removes CPLayer object from memory (for perfomance).
     *
     * @param playerName Name of the player.
     * @return CPlayer object which has been removed.
     */
    public CPLayer clearPlayerCache(String playerName) {
        return clanPlayersByName.remove(playerName.toLowerCase());
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

        updateClaim(claim);
    }

    /**
     * Updates information about the claim in the database.
     *
     * @param claim Claim which needs to be updated.
     */
    public void updateClaim(Claim claim) {
        storageManager.updateClaimClan(claim);
    }

    /**
     * @param x X coordinate
     * @param z Z coordinate
     * @return Claim object. Returns null if it has not been found (means that nobody has claimed this land).
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

    /**
     * @param cpLayer1 First player
     * @param cpLayer2 Second player
     * @return True if player 1 is in the same clan as player 2. Otherwise, false. If they are not members of any clan - returns false.
     */
    public boolean areInTheSameClan(CPLayer cpLayer1, CPLayer cpLayer2) {
        Clan clan1 = cpLayer1.getClan();
        Clan clan2 = cpLayer2.getClan();
        return clan1 != null && clan2 != null && clan1 == clan2;
    }

    /**
     * @param cpLayer1 First player
     * @param cpLayer2 Second player
     * @return True if player 1 is in the allied clan with clan of player 2. Otherwise, false. If they are not members of any clan - returns false.
     */
    public boolean areInAlliedClans(CPLayer cpLayer1, CPLayer cpLayer2) {
        Clan clan1 = cpLayer1.getClan();
        Clan clan2 = cpLayer2.getClan();
        return clan1 != null && clan2 != null && clan1.isAlliedWith(clan2);
    }

    /**
     * @param cpLayer CPLayer which will be promoted.
     * @return True if a player has been successfully promoted or false if it can not be promoted because he is on the highest rank
     * in the clan (highest clan role).
     */
    public boolean promoteClanPlayer(CPLayer cpLayer) {
        ClanRole newClanRole = ClanRole.getClanRoleByLevel(cpLayer.getClanRole().getLevel() + 1);
        if (newClanRole != null) {
            cpLayer.setClanRole(newClanRole);
            updatePlayer(cpLayer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param cpLayer CPLayer which will be demoted.
     * @return True if a player has been successfully demoted or false if it can not be demoted because he is on the lowest rank
     * in the clan (lowest clan role).
     */
    public boolean demoteClanPlayer(CPLayer cpLayer) {
        ClanRole newClanRole = ClanRole.getClanRoleByLevel(cpLayer.getClanRole().getLevel() - 1);
        if (newClanRole != null && newClanRole != ClanRole.OUTLAW) {
            cpLayer.setClanRole(newClanRole);
            updatePlayer(cpLayer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return Collection of all clans.
     */
    public Collection<Clan> getClans() {
        return clansByName.values();
    }
}
