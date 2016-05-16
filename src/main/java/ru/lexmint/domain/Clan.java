package ru.lexmint.domain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;

import java.util.HashSet;
import java.util.Set;

/**
 * Class which describes a clan.
 */
public class Clan {
    /**
     * Set of all clan's members.
     */
    private final Set<String> members = new HashSet<>();

    /**
     * Set of all players invited to the clan.
     */
    private final Set<String> invites = new HashSet<>();

    /**
     * Claims of a clan.
     */
    private final Set<Claim> claims = new HashSet<>();

    /**
     * Time when a clan was created.
     */
    private final long createdTime;

    /**
     * Set which contains clan allies (clans which are requested to be allies with this clan).
     */
    private final Set<Clan> alliances;

    /**
     * Name of a clan, tag.
     */
    private String name;

    /**
     * Description of a clan.
     */
    private String description;

    /**
     * Power of a clan, has an influence on territory claim.
     */
    private double power;

    /**
     * Home location of a clan.
     */
    private Location home;

    /**
     * Boost which is added to clan's power (may be fine to clan when player with negative power leaves it).
     */
    private double powerBoost;

    /**
     * Time when the last of clan members left the server. May be null.
     */
    private Long lastPlayed;

    /**
     * Number of times when this clan won matches on arena.
     */
    private int arenaWins;

    /**
     * Number of times when this clan lost matches on arena.
     */
    private int arenaDefeats;

    /**
     * Clan points (is used during the tournament).
     */
    private int points;

    /**
     * Basic constructor for creating a clan.
     *
     * @param name Name of a clan.
     */
    Clan(String name, long createdTime) {
        this.name = name;
        this.createdTime = createdTime;
        alliances = new HashSet<>();
    }

    /**
     * This constructor is used when importing clan from database.
     *
     * @param name
     * @param createdTime
     * @param description
     * @param arenaWins
     * @param arenaDefeats
     */
    Clan(String name, long createdTime, String description, int arenaWins, int arenaDefeats, int points) {
        this.name = name;
        this.createdTime = createdTime;
        this.description = description;
        this.arenaWins = arenaWins;
        this.arenaDefeats = arenaDefeats;
        alliances = new HashSet<>();
        this.points = points;
    }


    /**
     * Returns full name of a clan.
     *
     * @return The string containing full name of a clan.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns description of a clan.
     *
     * @return The string containing description of a clan. If clan has not set a description - it will return default
     * description which is configured in language config.
     */
    public String getDescription() {
        if (description == null) {
            return HSClans.instance.getLangConfig().getString("clan.description");
        }
        return description;
    }

    /**
     * Sets description of a clan.
     *
     * @param description A string containing description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns set of all clan's members (offline and online).
     *
     * @return The set containing all names of the clan's members.
     */
    public Set<String> getMembers() {
        return members;
    }

    /**
     * @return Set of CPlayer object of all members of this clan.
     */
    public Set<CPLayer> getCMembers() {
        Set<CPLayer> members = new HashSet<>();
        ClanManager clanManager = HSClans.instance.getClanManager();
        for (String member : this.members) {
            members.add(clanManager.getPlayer(member, false));
        }
        return members;
    }

    /**
     * Adds player to clan if he was not in it.
     *
     * @param playerName Name of a player.
     */
    void addPlayer(String playerName) {
        members.add(playerName);
    }

    /**
     * Removes player from clan if he is a clan member.
     *
     * @param playerName Name of a player.
     */
    void removePlayer(String playerName) {
        members.remove(playerName);
    }

    /**
     * Adds invitation for given playerName.
     *
     * @param playerName Name of a player.
     * @return Returns true if invitation for this player has been added. False if he has been already
     * invited to this clan.
     */
    public boolean addInvite(String playerName) {
        return invites.add(playerName);
    }

    /**
     * Removes invitation for given player.
     *
     * @param playerName Name of a player.
     * @return True if invitation has been successfully removed. If invitation for given player has not existed
     * in invitations set it returns false.
     */
    public boolean pullInvitation(String playerName) {
        return invites.remove(playerName);
    }

    /**
     * Checks member's participation in a clan.
     *
     * @param memberName Name of the player who is needed to be checked.
     * @return True if player with given name contains in the list of members of this clan.
     */
    public boolean containsMember(String memberName) {
        return members.contains(memberName);
    }

    /**
     * @return power of the clan
     */
    public double getPower() {
        updatePower();
        return power + powerBoost;
    }

    /**
     * @return Boost which is added to clan's power (may be even fine when player with negative power leaves clan).
     */
    public double getPowerBoost() {
        return powerBoost;
    }

    /**
     * Increases/decreases clan's power by given value.
     *
     * @param powerBoost Power which will be added to clan's power boost.
     */
    public void alterPowerBoost(double powerBoost) {
        this.powerBoost += powerBoost;
    }

    /**
     * Updates clan's power. Power of the clan relies on power of all member of the clan.
     */
    public void updatePower() {
        ClanManager clanManager = HSClans.instance.getClanManager();
        power = 0;
        for (String memberName : members) {
            CPLayer member = clanManager.getPlayer(memberName, false);
            power += member.getPower(true);
        }
    }

    /**
     * @return Maximal value of clan's power. Based on max power of all members of the clan.
     */
    public double getPowerMax() {
        ClanManager clanManager = HSClans.instance.getClanManager();
        double maxPower = 0;

        for (String memberName : members) {
            CPLayer member = clanManager.getPlayer(memberName, false);
            maxPower += member.getPowerMax();
        }
        return maxPower;
    }

    /**
     * @return Minimal value of clan's power.
     */
    public double getPowerMin() {
        ClanManager clanManager = HSClans.instance.getClanManager();
        double minPower = 0;
        for (String memberName : members) {
            CPLayer member = clanManager.getPlayer(memberName, false);
            minPower += member.getPowerMin();
        }
        return minPower;
    }

    /**
     * @return Rounded to int value of clan's power.
     */
    public int getPowerRounded() {
        return (int) getPower();
    }

    /**
     * @return Rounded to int value of clan's power boost.
     */
    public int getPowerBoostRounded() {
        return (int) getPowerBoost();
    }

    /**
     * @return Rounded to int maximal value of clan's power.
     */
    public int getPowerMaxRounded() {
        return (int) getPowerMax();
    }

    /**
     * @return Rounded to int minimal value of clan's power.
     */
    public int getPowerMinRounded() {
        return (int) getPowerMin();
    }

    /**
     * @return Number of clan claims.
     */
    public int getClaimsNumber() {
        return claims.size();
    }


    /**
     * @return If clan can hold claim (means that power of this clan is equal or higher than their claimed land
     * size) it returns true. Otherwise, false.
     */
    public boolean canHoldClaim() {
        return getPower() >= getClaimsNumber();
    }

    /**
     * @param claimsSize Number of claim lands.
     * @return If power of this clan is equal or higher than increased (by claimSize) value of claimed land number,
     * it returns true. Otherwise, false.
     */
    public boolean canClaim(int claimsSize) {
        return getPower() >= (getClaimsNumber() + claimsSize);
    }

    /**
     * Adds claim to clan's claims' list.
     *
     * @param claim The claim which is supposed to be added.
     * @return True if a claim has been successfully added and false if it has already been in clan's claim list.
     */
    boolean addClaim(Claim claim) {
        return claims.add(claim);
    }

    /**
     * Removes claim from its claims list.
     *
     * @param claim The claim which is supposed to be removed.
     * @return True if a claim has been found and successfully removed. If not found - false.
     */
    boolean removeClaim(Claim claim) {
        return claims.remove(claim);
    }

    /**
     * Removes all claims of the clan.
     */
    void removeAllClaims() {
        claims.clear();
    }

    /**
     * @return Set of clan's claims.
     */
    public Set<Claim> getClaims() {
        return claims;
    }

    /**
     * @return Home location of a clan. May be null if a clan has not set its home.
     */
    public Location getHome() {
        return home;
    }

    /**
     * @param home New home location for a clan.
     */
    public void setHome(Location home) {
        this.home = home;
    }

    /**
     * @return True if a clan has got home. Otherwise, if it has not been set, false.
     */
    public boolean hasHome() {
        return home != null;
    }

    /**
     * @return Time when a clan was created.
     */
    public long getCreatedTime() {
        return createdTime;
    }

    /**
     * @return Number of days passed since a clan was created.
     */
    public int getDaysSinceCreated() {
        return (int) ((System.currentTimeMillis() - getCreatedTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * @return True if a clan has a leader, otherwise false.
     */
    public boolean hasLeader() {
        return getLeadersNumber() >= 1;
    }

    /**
     * @return Number of leaders in clan.
     */
    public int getLeadersNumber() {
        ClanManager clanManager = HSClans.instance.getClanManager();
        int leaders = 0;
        for (String member : members) {
            if (clanManager.getPlayer(member, false).getClanRole() == ClanRole.LEADER) {
                leaders++;
            }
        }
        return leaders;
    }

    /**
     * @return Number of clan members.
     */
    public int getMembersSize() {
        return members.size();
    }

    /**
     * @return Set of Players (clan members) who is online on server now.
     */
    public Set<Player> getMembersOnline() {
        Set<Player> playerSet = new HashSet<>();
        for (String member : members) {
            Player player = Bukkit.getPlayerExact(member);
            if (player != null) {
                playerSet.add(player);
            }
        }
        return playerSet;
    }

    /**
     * @return True if even though one player of this clan is online.
     */
    public boolean hasPlayersOnline() {
        for (String member : members) {
            Player player = Bukkit.getPlayerExact(member);
            if (player != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Set of Players (clan members) who is offline now.
     */
    public Set<OfflinePlayer> getMembersOffline() {
        Set<OfflinePlayer> playerSet = new HashSet<>();
        for (String member : members) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(member);
            if (!player.isOnline()) {
                playerSet.add(player);
            }
        }
        return playerSet;
    }

    /**
     * @param clan Clan which will be allied with this.
     * @return True if clan has been added to alliances. False, if clan has more than allies limit already or
     * if set of this clan's alliances has already
     * contained given clan.
     */
    public boolean addAlliance(Clan clan) {
        return alliances.size() < HSClans.instance.getSettings().getInt("clan.max-allies") && alliances.add(clan);
    }

    /**
     * @param clan Clan which will be removed from this clan's alliances.
     * @return True if clan has been successfully removed from this clan's alliances. False, if it has not
     * existed in alliances.
     */
    public boolean removeAlliance(Clan clan) {
        return alliances.remove(clan);
    }

    /**
     * @param clan Clan which needs to be checked for ally with this.
     * @return True if the clan is allied with given clan. Otherwise, false.
     */
    public boolean isAlliedWith(Clan clan) {
        return clan != null && alliances.contains(clan) && clan.isRequestingAllyWith(this);
    }

    /**
     * @param clan Clan which needs to be checked for ally request with this.
     * @return True if this clan is requesting alliance with given. Otherwise (if not requesting or already allied), false.
     */
    public boolean isRequestingAllyWith(Clan clan) {
        return alliances.contains(clan);
    }

    /**
     * @return Set of clans which this clan is actually (two sides has accepted an ally) allied with.
     */
    public Set<Clan> getAlliances() {
        Set<Clan> allies = new HashSet<>();
        for (Clan clan : alliances) {
            if (isAlliedWith(clan)) {
                allies.add(clan);
            }
        }
        return allies;
    }

    /**
     * HSR - High Sky Rate - is a scale which defines player or clan's skills and experience on server.
     *
     * @return High Sky rate.
     */
    public int getHSRate() {
        ClanManager clanManager = HSClans.instance.getClanManager();
        double hsRate = 0.0;
        for (String member : getMembers()) {
            hsRate += clanManager.getPlayer(member, false).getHSRate();
        }

        if (getMembersSize() <= 3) {
            /* To prevent clans with one cool players that are top. */
            hsRate /= getMembersSize() * 3;
        } else {
            hsRate /= getMembersSize();
        }

        hsRate += getExpRate();
        // hsRate += getArenaRate();

        return (int) (hsRate * 100);
    }

    public double getExpRate() {
        double expRate = getDaysSinceCreated() / 5d;
        if (expRate > 5) {
            expRate = 5.0;
        }
        return expRate;
    }

    public double getArenaRate() {
        double arenaRate;
        if (getArenaDefeats() != 0) {
            arenaRate = getArenaWins() / getArenaDefeats();
        } else {
            arenaRate = getArenaWins();
        }

        if (arenaRate > 5) {
            arenaRate = 5.0;
        }

        return arenaRate;
    }

    public Level getLevel() {
        return Level.getLevelByRate(Level.LevelType.CLAN, getHSRate());
    }

    /**
     * @return Time when the last of clan members left the server. May be null if not have been initialized.
     */
    public Long getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public void incrementArenaWins() {
        arenaWins++;
        HSClans.instance.getClanManager().updateClan(this);
    }

    public void incrementArenaDefeats() {
        arenaDefeats++;
        HSClans.instance.getClanManager().updateClan(this);
    }

    public int getArenaWins() {
        return arenaWins;
    }

    public int getArenaDefeats() {
        return arenaDefeats;
    }

    public void incrementPoints() {
        points++;
        HSClans.instance.getClanManager().updateClan(this);
    }

    public int getPoints() {
        return points;
    }
}