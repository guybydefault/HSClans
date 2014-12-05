package ru.lexmint.domain;

import ru.lexmint.HSClans;

import java.util.HashSet;
import java.util.Set;

/**
 * Class which describes a clan.
 */
public class Clan {
    /**
     * Name of a clan, tag.
     */
    private String name;

    /**
     * Description of a clan.
     */
    private String description;

    /**
     * Set of all clan's members.
     */
    private Set<String> members = new HashSet<>();

    /**
     * Set of all players invited to the clan.
     */
    private Set<String> invites = new HashSet<>();

    /**
     * League of the clan.
     */
    private ClanLeague clanLeague;

    /**
     * Power of a clan, has an influence on territory claim.
     */
    private double power;

    /**
     * Power bonus which is added to basic power of all players in a clan (basic power of a player is configured in config).
     */
    private double powerBoost;

    /**
     * Basic constructor for creating a clan.
     *
     * @param name Name of a clan.
     */
    Clan(String name, ClanLeague clanLeague) {
        this.name = name;
        this.clanLeague = clanLeague;
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
     * @return The string containing description of a clan.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of a clan.
     *
     * @param description A string containing description.
     */
    void setDescription(String description) {
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
     * @return league of the clan
     */
    public ClanLeague getClanLeague() {
        return clanLeague;
    }

    /**
     * @return power of the clan
     */
    public double getPower() {
        updatePower();
        return power;
    }

    /**
     * Updates clan's power. Power of the clan relies on power of all member of the clan.
     */
    public void updatePower() {
        ClanManager clanManager = HSClans.instance.getClanManager();
        power = 0;
        for (String memberName : members) {
            CPLayer member = clanManager.getPlayer(memberName, false);
            power += member.getPower();
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
        return (int) Math.round(getPower());
    }

    /**
     * @return Rounded to int maximal value of clan's power.
     */
    public int getPowerMaxRounded() {
        return (int) Math.round(getPowerMax());
    }

    /**
     * @return Rounded to int minimal value of clan's power.
     */
    public int getPowerMinRounded() {
        return (int) Math.round(getPowerMin());
    }
}
