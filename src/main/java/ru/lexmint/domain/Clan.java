package ru.lexmint.domain;

import java.util.List;

/**
 * Class which describes a clan.
 */
public class Clan {
    /**
     * Full name of a clan.
     */
    private String name;

    /**
     * Short name of a clan.
     */
    private String tag;

    /**
     * List of all clan's members.
     */
    private List<CPLayer> members;

    /**
     * Returns full name of a clan.
     * @return The string containing full name of a clan.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns tag of a clan.
     * @return The string containing short name (tag) of a clan.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Returns list of all clan's members (offline and online).
     * @return The list containing all of the clan's members.
     */
    public List<CPLayer> getMembers() {
        return members;
    }
}
