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
    // TODO Default description of a clan
    private String description;

    /**
     * Set of all clan's members.
     */
    private Set<CPLayer> members = new HashSet<>();

    /**
     * Basic constructor for creating a clan.
     * @param name Name of a clan.
     */
    public Clan(String name) {
        this.name = name;
    }

    /**
     * Returns full name of a clan.
     * @return The string containing full name of a clan.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns description of a clan.
     * @return The string containing description of a clan.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of a clan.
     * @param description A string containing description.
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns set of all clan's members (offline and online).
     * @return The set containing all of the clan's members.
     */
    public Set<CPLayer> getMembers() {
        return members;
    }

    /**
     * Adds player to clan if he was not in it.
     * @param player CPLayer object of a player.
     */
    void addPlayer(CPLayer player) {
        members.add(player);
    }
}
