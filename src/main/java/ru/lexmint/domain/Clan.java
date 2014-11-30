package ru.lexmint.domain;

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
     * Basic constructor for creating a clan.
     *
     * @param name Name of a clan.
     */
    public Clan(String name) {
        this.name = name;
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
    boolean addInvite(String playerName) {
        return invites.add(playerName);
    }

    /**
     * Removes invitation for given player.
     * @param playerName Name of a player.
     * @return True if invitation has been successfully removed. If invitation for given player has not existed
     * in invitations set it returns false.
     */
    boolean pullInvitation(String playerName) {
        return invites.remove(playerName);
    }

    /**
     * Checks member's participation in a clan.
     * @param memberName Name of the player who is needed to be checked.
     * @return True if player with given name contains in the list of members of this clan.
     */
    public boolean containsMember(String memberName) { return members.contains(memberName); }
}
