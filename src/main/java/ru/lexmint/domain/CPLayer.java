package ru.lexmint.domain;

import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.stats.PlayerStats;

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
     * Contains player kills, deaths and counts kdr.
     */
    private PlayerStats stats;

    /**
     * Time when player joined the server first.
     */
    private final long firstPlayed;

    /**
     * Hours of time while player has been playing on server.
     */
    private double hoursPlayed;


    /**
     * Constructor for player without clan.
     *
     * @param name  Name of the player.
     * @param power Starting value of player's power.
     */
    CPLayer(String name, double power) {
        this.name = name;
        clanRole = ClanRole.OUTLAW;
        clanLeague = ClanLeague.LOW;
        this.power = power;
        lastPowerUpdateTime = System.currentTimeMillis();
        stats = new PlayerStats(0, 0);
        firstPlayed = System.currentTimeMillis();
        hoursPlayed = 0;
    }

    /**
     * Standard constructor for creating a CPLayer.
     *
     * @param name                Name of the player.
     * @param clan                Player's clan.
     * @param clanRole            Player's role in the given clan.
     * @param clanLeague          League of the player.
     * @param power               Power of the player.
     * @param powerBoost          Boost which is added to player's basic power.
     * @param lastPowerUpdateTime Last time when player's power was updated.
     * @param kills               Number of kills this player has made.
     * @param deaths              Number of player's deaths.
     */
    CPLayer(String name, Clan clan, ClanRole clanRole, ClanLeague clanLeague, double power, double powerBoost, long lastPowerUpdateTime, int kills, int deaths, long firstPlayed, double hoursPlayed) {
        this.name = name;
        this.clan = clan;
        this.clanRole = clanRole;
        this.clanLeague = clanLeague;
        this.power = power;
        this.powerBoost = powerBoost;
        this.lastPowerUpdateTime = lastPowerUpdateTime;
        this.stats = new PlayerStats(kills, deaths);
        this.firstPlayed = firstPlayed;
        this.hoursPlayed = hoursPlayed;
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
     * Returns if player can join clan.
     *
     * @return True if player can join clan. Otherwise (if he is not allowed to or he is already in clan), false.
     */
    public boolean canJoinClan() {
        return (clanRole == ClanRole.OUTLAW);
    }

    /**
     * @return True if player has clan, otherwise false.
     */
    public boolean hasClan() {
        return clan != null;
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

    /**
     * @return Object which contains player kills, deaths, count KDR (KillDeath rate).
     */
    public PlayerStats getStats() {
        return stats;
    }

    /**
     * @return Time when player joined server for the first time.
     */
    public long getFirstPlayed() {
        return firstPlayed;
    }

    /**
     * @return Days which have passed since this player joined server for the first time.
     */
    public int getDaysSinceFirstPlayed() {
        return (int) (System.currentTimeMillis() - firstPlayed) / 1000 / 60 / 60 / 24;
    }

    /**
     * Increases/decreases player's time on server.
     *
     * @param alter Hours which will be added to player's summary hours played.
     */
    public void alterHoursPlayed(double alter) {
        hoursPlayed += alter;
    }

    /**
     * @return Hours of time while player has been playing on server.
     */
    public double getHoursPlayed() {
        return hoursPlayed;
    }

    /**
     *
     * @return Rounded time while player has been playing on server.
     */
    public int getHoursPlayedRounded() {return (int) Math.round(hoursPlayed); }


}
