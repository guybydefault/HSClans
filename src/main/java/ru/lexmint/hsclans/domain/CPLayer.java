package ru.lexmint.hsclans.domain;

import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;

/**
 * Class, describing a player who is or was in a clan.
 */
public class CPLayer {
    /**
     * In-game name of a player.
     */
    private final String name;

    /**
     * Time when player joined the server first.
     */
    private final long firstPlayed;

    /**
     * Clan of a player. May be null.
     */
    private Clan clan;

    /**
     * Role of a player in a clan.
     */
    private ClanRole clanRole;

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
     * Time when player was seen on server for the last time.
     */
    private long lastPlayed;

    /**
     * Hours of time while player has been playing on server.
     */
    private double hoursPlayedTotal;

    /**
     * Hours of time while player has been playing on server during this week.
     */
    private double hoursPlayedWeek;

    /**
     * Hours of time while player has been playing on server during previous week.
     */
    private double hoursPlayedPreviousWeek;

    /**
     * Number of kills player has made.
     */
    private int kills;

    /**
     * Number of points player has gained from killing other players.
     */
    private int points;

    /**
     * Number of player's deaths.
     */
    private int deaths;

    /**
     * Number of times when player won matches on arena.
     */
    private int arenaWins;

    /**
     * Number of times when player lost matches on arena.
     */
    private int arenaDefeats;

    /**
     * If it's true - player is participating in the tournament now. False - he has been eliminated or he is not participating.
     */
    private boolean tournamentState;

    /**
     * High Sky Rate (for the current period of time - week, etc.)
     */
    private int HSR;

    /**
     * High Sky Rate which is displayed everywhere to show skills of the player. It's HSR for the end of the last period of time (week).
     */
    private int HSRView;

    /**
     * Constructor for player without clan.
     *
     * @param name  Name of the player.
     * @param power Starting value of player's power.
     */
    CPLayer(String name, double power) {
        this.name = name;
        clanRole = ClanRole.OUTLAW;
        this.power = power;
        lastPowerUpdateTime = System.currentTimeMillis();
        firstPlayed = System.currentTimeMillis();
        lastPlayed = System.currentTimeMillis();
        hoursPlayedTotal = 0;
        hoursPlayedWeek = 0;
        hoursPlayedPreviousWeek = 0;
        HSR = 0;
        HSRView = 0;
        kills = 0;
        points = 0;
        deaths = 0;
        arenaWins = 0;
        arenaDefeats = 0;
        tournamentState = false;
    }

    /**
     * Standard constructor for creating a CPLayer.
     *
     * @param name                    Name of the player.
     * @param clan                    Player's clan.
     * @param clanRole                Player's role in the given clan.
     * @param power                   Power of the player.
     * @param powerBoost              Boost which is added to player's basic power.
     * @param lastPowerUpdateTime     Last time when player's power was updated.
     * @param kills                   Number of kills this player has made.
     * @param HSR                     Rate of the player.
     * @param deaths                  Number of player's deaths.
     * @param points                  Points player has gained from killing other players.
     * @param hoursPlayedTotal        Hours of time while player has been playing on server.
     * @param hoursPlayedWeek         Hours of time while player has been playing on server during this week.
     * @param hoursPlayedPreviousWeek Hours of time while player has been playing on server during previous week.
     */
    CPLayer(String name, Clan clan, ClanRole clanRole, double power, double powerBoost, long lastPowerUpdateTime, int kills, int points, int deaths, long firstPlayed, long lastPlayed, double hoursPlayedTotal, double hoursPlayedWeek, double hoursPlayedPreviousWeek, int HSR, int HSRView, int arenaWins, int arenaDefeats, boolean tournamentState) {
        this.name = name;
        this.clan = clan;
        this.clanRole = clanRole;
        this.power = power;
        this.powerBoost = powerBoost;
        this.lastPowerUpdateTime = lastPowerUpdateTime;
        this.kills = kills;
        this.points = points;
        this.deaths = deaths;
        this.firstPlayed = firstPlayed;
        this.hoursPlayedTotal = hoursPlayedTotal;
        this.hoursPlayedWeek = hoursPlayedWeek;
        this.hoursPlayedPreviousWeek = hoursPlayedPreviousWeek;
        this.HSR = HSR;
        this.HSRView = HSRView;
        this.lastPlayed = lastPlayed;
        this.arenaWins = arenaWins;
        this.arenaDefeats = arenaDefeats;
        this.tournamentState = tournamentState;
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
     * Sets player's role in clan.
     *
     * @param clanRole Player's role in the clan.
     */
    void setClanRole(ClanRole clanRole) {
        this.clanRole = clanRole;
    }

    /**
     * Removes data of clan for this player.
     */
    public void removeFromClan() {
        clanRole = ClanRole.OUTLAW;
        clan = null;
    }

    /**
     * @return True if player has clan, otherwise false.
     */
    public boolean hasClan() {
        return clan != null;
    }

    /**
     * Get power of a player. Before getting it, this method updates power of a player to actual value.
     *
     * @param updatePower Set TRUE if you want to know actual player's power. FALSE - only for database.
     * @return power of a player
     */
    public double getPower(boolean updatePower) {
        if (updatePower) {
            updatePower();
        }
        return power;
    }

    /**
     * Manages power updating depending on config values and behavior described in config.
     */
    private void updatePower() {
        if (!isOnline()) {
            if (!HSClans.instance.getSettings().getBoolean("power.regen-offline")) {
                losePowerFromBeingOffline();
                return;
            }
        }

        long now = System.currentTimeMillis();
        long millisPassed = now - lastPowerUpdateTime;
        lastPowerUpdateTime = now;

        Player player = getPlayer();
        if (player != null && player.isDead()) {
            /* Dead players can't regen their power! */
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
        // TODO it's not necessary
        HSClans.instance.getClanManager().updatePlayer(this);
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
    public void losePowerFromBeingOffline() {
        double powerOfflineLossPerDay = HSClans.instance.getSettings().getDouble("power.offline.loss-per-day");
        double powerOfflineLossLimit = HSClans.instance.getSettings().getDouble("power.offline.loss-limit");
        if (powerOfflineLossPerDay > 0.0) {
            long now = System.currentTimeMillis();
            long millisPassed = now - lastPowerUpdateTime;
            double hoursPassed = millisPassed / 1000d / 60 / 60;

            if (hoursPassed < 24) {
                return;
            }

            lastPowerUpdateTime = now;

            double loss = (hoursPassed / 24) * powerOfflineLossPerDay;

            if (power < powerOfflineLossLimit) {
                return;
            } else if (power - loss < powerOfflineLossLimit) {
                /** Equal to power = powerOfflineLossLimit */
                alterPower(power - powerOfflineLossLimit);
            } else {
                alterPower(-loss);
            }
        }
    }

    public void setLastPowerUpdateTime(long time) {
        lastPowerUpdateTime = time;
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
     * @return Rounded to int value of player's power (actual power, this method updates it)
     */
    public int getPowerRounded() {
        /** Method getPowerRounded() is used only in game for displaying info - so we need to update power's value. */
        return (int) Math.round(this.getPower(true));
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
    public void losePowerOnDeath() {
        updatePower();
        alterPower(-HSClans.instance.getSettings().getDouble("power.loss-per-death"));
    }

    /**
     * @return Player object of a player. May be null if he is offline.
     */
    public Player getPlayer() {
        return HSClans.instance.getServer().getPlayerExact(getName());
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
     * @return Time when player joined server for the first time.
     */
    public long getFirstPlayed() {
        return firstPlayed;
    }

    /**
     * @return Amount of ime when player was seen on server for the last time
     * (if he's online - returns time of his last quit from server).
     */
    public long getLastPlayed() {
        return lastPlayed;
    }

    /**
     * @param lastPlayed Time when player was seen on server for the last time
     */
    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    /**
     * @return Amount of time (in hours) when player was saw on server for the last time
     * (if he's online - returns time of his last quit from server).
     */
    public double getHoursSinceLastPlayed() {
        return (System.currentTimeMillis() - getLastPlayed()) / 1000d / 60 / 60;
    }

    /**
     * @return Days which have passed since this player joined server for the first time.
     */
    public int getDaysSinceFirstPlayed() {
        return (int) ((System.currentTimeMillis() - firstPlayed) / 1000 / 60 / 60 / 24);
    }

    /**
     * Increases/decreases player's time on server.
     *
     * @param alter Hours which will be added to player's summary hours played.
     */
    public void alterHoursPlayed(double alter) {
        hoursPlayedTotal += alter;
        hoursPlayedWeek += alter;
    }


    /**
     * Sets hours played week to zero.
     */
    public void resetHoursPlayedWeek() {
        setHoursPlayedPreviousWeek(getHoursPlayedWeek());
        hoursPlayedWeek = 0;
    }

    public void setHoursPlayedPreviousWeek(double hoursPlayedPreviousWeek) {
        this.hoursPlayedPreviousWeek = hoursPlayedPreviousWeek;
    }

    /**
     * @return Hours of time while player has been playing on server.
     */
    public double getHoursPlayedTotal() {
        return hoursPlayedTotal;
    }

    /**
     * @return Rounded time while player has been playing on server.
     */
    public int getHoursPlayedTotalRounded() {
        return (int) Math.round(getHoursPlayedTotal());
    }

    /**
     * @return Hours of time while player has been playing on server during the week.
     */
    public double getHoursPlayedWeek() {
        return hoursPlayedWeek;
    }

    /**
     * @return Hours of time while player has been playing on server during the previous week.
     */
    public double getHoursPlayedPreviousWeek() {
        return hoursPlayedPreviousWeek;
    }

    /**
     * @return Rounded time while player has been playing on server during the week.
     */
    public int getHoursPlayedWeekRounded() {
        return (int) Math.round(getHoursPlayedWeek());
    }

    /**
     * @return Rounded time while player has been playing on server during the previous week.
     */
    public int getHoursPlayedPreviousWeekRounded() {
        return (int) Math.round(getHoursPlayedPreviousWeek());
    }

    /**
     * @return Number of kills player has made.
     */
    public int getKills() {
        return kills;
    }

    /**
     * Increments number of kills by 1.
     */
    public void incrementKills() {
        kills++;
    }

    /**
     * Alters points given to player when he kills another player. Feature is in number of these points.
     * In case the player kills a player whose level is bigger than him, the player is given more points
     * (difference between victim's level and the player's, killer's). In case the player kills a player
     * with the same level of points - he is given just one point. In other cases - zero points.
     *
     * @param victim Player who was killed by this one.
     */
    public void alterPoints(CPLayer victim) {
        if (victim.getLevel().getLevel() > getLevel().getLevel()) {
            points += victim.getLevel().getLevel() - getLevel().getLevel();
        } else if (victim.getLevel().getLevel() == getLevel().getLevel()) {
            points++;
        }
    }


    /**
     * Increments number of deaths by 1.
     */
    public void incrementDeaths() {
        deaths++;
    }

    /**
     * @return Number of points player has gained from killing other players.
     */
    public int getPoints() {
        return points;
    }

    /**
     * @return Number of player's deaths.
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * @param cPlayer Player for which we need to check relation ship.
     * @return True if cpLayer is enemy. Otherwise (if alliance or same clan) false.
     */
    public boolean isEnemy(CPLayer cPlayer) {
        return !cPlayer.hasClan() || (cPlayer.getClan() != getClan() && !cPlayer.getClan().isAlliedWith(getClan()));
    }

    public boolean isAlly(CPLayer cpLayer) {
        return hasClan() && cpLayer.hasClan() && cpLayer.getClan().isAlliedWith(getClan());
    }

    public boolean isInTheSameClan(CPLayer cpLayer) {
        return hasClan() && cpLayer.hasClan() && cpLayer.getClan().equals(getClan());
    }

    /**
     * This HSR is temporary (for the current week)! This Rate is changing during the week. So, to display
     * player's skills it's better to use HSRateView.
     * HSR - High Sky Rate - is a scale which defines player or clan's skills and experience on server.
     *
     * @return High Sky rate.
     */
    public int getHSRate() {
        return HSR;
    }

    /**
     * HSR - High Sky Rate - is a scale which defines player or clan's skills and experience on server.
     * This HSR is actual for the previous period of time (week) and should be used to display player's skills.
     *
     * @return High Sky rate.
     */
    public int getHSRateView() {
        return HSRView;
    }

    public Level getLevel() {
        return Level.getLevelByRate(Level.LevelType.PLAYER, getHSRateView());
    }

    public int getPvPRate() {
        int prevExpRate = getExpRate(getHoursPlayedTotal() - getHoursPlayedWeek() - getHoursPlayedPreviousWeek());
        int prevOnlineRate = getOnlineRate(getHoursPlayedPreviousWeek());
        int pvpRate = getHSRate() - prevExpRate - prevOnlineRate;
        if (pvpRate > 500) {
            pvpRate = 500;
        } else if (pvpRate < 0) {
            pvpRate = 0;
        }
        return pvpRate;
    }

    /**
     * @param hours which are taken into account.
     * @return
     */
    public int getOnlineRate(double hours) {
        /* Max valued hours = 21. Max online rate = 500. Then we need 4.2 here. */
        int onlineRate = (int) ((hours / 4.2) * 100);
        if (onlineRate > 500) {
            onlineRate = 500;
        }
        return onlineRate;
    }

    /**
     * This method must be used in the end of period (week) when time resets and stats needs to be updated.
     * Updates (resets) HSR rate of the player. IMPORTANT! This method also does a request to database so there
     * is no need to update database by hand.
     */
    public void updateHSRate() {
        if (getHSRate() > 1500) {
            HSRView = 1500;
        } else {
            HSRView = getHSRate();
        }
        HSR = getExpRate(getHoursPlayedTotal() - getHoursPlayedWeek()) + getOnlineRate(getHoursPlayedWeek()) + getPvPRate();
    }

    public void alterHSRate(int delta) {
        HSR += delta;
    }

    /**
     * @param hours which are taken into account.
     * @return
     */
    public int getExpRate(double hours) {
        int expRate = (int) ((hours / 50d) * 100);
        if (expRate > 500) {
            expRate = 500;
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

    public void incrementArenaWins() {
        arenaWins++;
        HSClans.instance.getClanManager().updatePlayer(this);
    }

    public void incrementArenaDefeats() {
        arenaDefeats++;
        HSClans.instance.getClanManager().updatePlayer(this);
    }

    public int getArenaWins() {
        return arenaWins;
    }

    public int getArenaDefeats() {
        return arenaDefeats;
    }

    public boolean getTournamentState() {
        return tournamentState;
    }

    /**
     * Makes player join the tournament. Updates info in database!
     */
    public void joinTournament() {
        tournamentState = true;
        // TODO it's not necessary
        HSClans.instance.getClanManager().updatePlayer(this);
    }

    /**
     * Makes player leave the tournament. Does not update info in database, you need to do it by yourself.
     */
    public void leaveTournament() {
        tournamentState = false;
    }
}
