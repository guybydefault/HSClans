package ru.lexmint.domain;

import org.bukkit.entity.Player;
import ru.lexmint.HSClans;

/**
 * Class, describing a player who is or was in a clan.
 */
public class CPLayer {
    /**
     * In-game name of a player.
     */
    private final String name;

    /**
     * Clan of a player. May be null.
     */
    private Clan clan;

    /**
     * Role of a player in a clan.
     */
    private ClanRole clanRole;

    /**
     * League of the player.
     */
    private ClanLeague clanLeague;

    /**
     * Power of a player, has an influence on clan in which player is a member.
     */
    private double power;

    /**
     * Power bonus which is added to basic power (basic power is configured in config).
     */
    private double powerBoost;

    /**
     * Last time when player's power was updated.
     */
    private long lastPowerUpdateTime;


    /**
     * Constructor for player without clan.
     *
     * @param name Name of the player.
     */
    CPLayer(String name) {
        this.name = name;
        clanRole = ClanRole.OUTLAW;
        clanLeague = ClanLeague.LOW;
    }

    /**
     * Standard constructor for creating a CPLayer.
     *
     * @param name       Name of the player.
     * @param clan       Player's clan.
     * @param clanRole   Player's role in the given clan.
     * @param clanLeague League of the player.
     */
    CPLayer(String name, Clan clan, ClanRole clanRole, ClanLeague clanLeague, double power, double powerBoost, long lastPowerUpdateTime) {
        this.name = name;
        this.clan = clan;
        this.clanRole = clanRole;
        this.clanLeague = clanLeague;
        this.power = power;
        this.powerBoost = powerBoost;
        this.lastPowerUpdateTime = lastPowerUpdateTime;
    }

    /**
     * Return in-game name of a player.
     *
     * @return String containing in-game name of a player.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the clan in which player exists.
     *
     * @return Clan object (clan of this player).
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * Sets clan for this CPLayer.
     *
     * @param clan Player's clan.
     */
    void setClan(Clan clan) {
        this.clan = clan;
    }


    /**
     * Return player's role in a clan.
     *
     * @return ClanRole object with player's role in a clan.
     */
    public ClanRole getClanRole() {
        return clanRole;
    }

    /**
     * Removes data of clan for this player.
     */
    public void removeFromClan() {
        clanRole = ClanRole.OUTLAW;
        clan = null;
    }

    /**
     * Sets player's role in clan.
     *
     * @param clanRole Player's role in the clan.
     */
    void setClanRole(ClanRole clanRole) {
        this.clanRole = clanRole;
    }

    /**
     * Returns player's status in a clan.
     *
     * @return True if player is in clan. Otherwise, false.
     */
    public boolean isInClan() {
        return (clanRole == ClanRole.OUTLAW);
    }

    /**
     * @return Clan league of this player.
     */
    public ClanLeague getClanLeague() {
        return clanLeague;
    }

    /**
     * Get power of a player. Before getting it, this method updates power of a player to actual value.
     *
     * @return power of a player
     */
    public double getPower() {
        updatePower();
        return power;
    }

    /**
     * Manages power updating depending on config values and behavior described in config.
     */
    void updatePower() {
        if (!isOnline()) {
            losePowerFromBeingOffline();
            if (!HSClans.instance.getSettings().getBoolean("power.regen-offline")) {
                return;
            }
        }
        long now = System.currentTimeMillis();
        long millisPassed = now - lastPowerUpdateTime;
        lastPowerUpdateTime = now;

        Player player = getPlayer();
        if (player != null && player.isDead()) {
            // Dead players can't regen their power!
            return;
        }

        int millisPerMinute = 60 * 1000;
        alterPower(millisPassed * HSClans.instance.getSettings().getDouble("power.per-minute") / millisPerMinute);

    }

    /**
     * Modifies power with given value. If power is getting out its scale - it takes maximal or minimal power values.
     *
     * @param delta Difference between player's power before and after.
     */
    void alterPower(double delta) {
        power += delta;
        if (power > getPowerMax()) {
            power = getPowerMax();
        } else if (power < getPowerMin()) {
            power = getPowerMin();
        }
    }

    /**
     * Checks player online status getting his Player object. If it is null - returns false. Otherwise, true.
     *
     * @return Online player's status on server.
     */
    public boolean isOnline() {
        return (getPlayer() != null);
    }

    /**
     * Manages power loosing from being offline. Each day of player's offline may cost him power to decrease. Power loss per day
     * and power loss limit are configured in config.
     */
    private void losePowerFromBeingOffline() {
        double powerOfflineLossPerDay = HSClans.instance.getSettings().getDouble("power.offline.loss-per-day");
        double powerOfflineLossLimit = HSClans.instance.getSettings().getDouble("power.offline.loss-limit");
        if (powerOfflineLossPerDay > 0.0 && power > powerOfflineLossLimit) {
            long now = System.currentTimeMillis();
            long millisPassed = now - lastPowerUpdateTime;
            lastPowerUpdateTime = now;

            double loss = millisPassed * powerOfflineLossPerDay / (24 * 60 * 60 * 1000);

            if (power - loss < powerOfflineLossLimit) {
                power = powerOfflineLossLimit;
            } else {
                alterPower(-loss);
            }

        }
    }

    /**
     * @return Maximal value of power. Based on config value plus power boost of this player.
     */
    public double getPowerMax() {
        return HSClans.instance.getSettings().getDouble("power.max-value") + powerBoost;
    }

    /**
     * @return Minimal value of power from config.
     */
    public double getPowerMin() {
        return HSClans.instance.getSettings().getDouble("power.min-value");
    }

    /**
     * @return Rounded to int value of player's power.
     */
    public int getPowerRounded() {
        return (int) Math.round(this.getPower());
    }

    /**
     * @return Rounded to int maximal value of player's power.
     */
    public int getPowerMaxRounded() {
        return (int) Math.round(this.getPowerMax());
    }

    /**
     * @return Rounded to int minimal value of player's power.
     */
    public int getPowerMinRounded() {
        return (int) Math.round(this.getPowerMin());
    }

    /**
     * Manages player's power update on his death.
     */
    public void onDeath() {
        updatePower();
        alterPower(-HSClans.instance.getSettings().getDouble("power.loss-per-death"));
    }

    /**
     * @return Player object of a player. May be null if he is offline.
     */
    public Player getPlayer() {
        return HSClans.instance.getServer().getPlayer(getName());
    }

    /**
     * @return boost to player's power (bonus)
     */
    public double getPowerBoost() {
        return powerBoost;
    }

    /**
     * @return time when player's power was updated
     */
    public long getLastPowerUpdateTime() {
        return lastPowerUpdateTime;
    }


}
