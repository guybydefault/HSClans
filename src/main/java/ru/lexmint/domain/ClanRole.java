package ru.lexmint.domain;

import ru.lexmint.HSClans;

/**
 * Class describing role of a player in clan.
 */
public enum ClanRole {
    /**
     * Outlaw - player without a clan
     */
    OUTLAW(0, HSClans.instance.getLangConfig().getString("clan.roles.outlaw.name"), HSClans.instance.getLangConfig().getString("clan.roles.outlaw.tag")),

    NEWBIE(1, HSClans.instance.getLangConfig().getString("clan.roles.newbie.name"), HSClans.instance.getLangConfig().getString("clan.roles.newbie.tag")),
    USER(2, HSClans.instance.getLangConfig().getString("clan.roles.user.name"), HSClans.instance.getLangConfig().getString("clan.roles.user.tag")),
    MODERATOR(3, HSClans.instance.getLangConfig().getString("clan.roles.moderator.name"), HSClans.instance.getLangConfig().getString("clan.roles.moderator.tag")),
    LEADER(4, HSClans.instance.getLangConfig().getString("clan.roles.leader.name"), HSClans.instance.getLangConfig().getString("clan.roles.leader.tag"));

    /**
     * Localized name of ClanRole.
     */
    private final String name;

    /**
     * Level of this role's permissions (ladder). Allows to compare different clan roles.
     */
    private final int level;

    /**
     * Shorten name of clan role which can be used in chat.
     */
    private final String tag;

    /**
     * Basic constructor for creating enums with localized names.
     *
     * @param name Localized name of ClanRole.
     */
    ClanRole(int level, String name, String tag) {
        this.level = level;
        this.name = name;
        this.tag = tag;
    }

    /**
     * Returns level of this role's permissions (ladder).
     *
     * @return Int - level of this role's permissions.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get localised name of this clan role for using in messages, etc.
     *
     * @return String. Localised name of the clan role.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Shorten name of clan role which can be used in chat.
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param level Level of the ClanRole.
     * @return First appearance of the ClanRole among all ClanRole values by given level. If not found - returns null.
     */
    public static ClanRole getClanRoleByLevel(int level) {
        for (ClanRole clanRole : ClanRole.values()) {
            if (clanRole.getLevel() == level) {
                return clanRole;
            }
        }
        return null;
    }
}
