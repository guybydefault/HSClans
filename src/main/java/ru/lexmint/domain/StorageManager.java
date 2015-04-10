package ru.lexmint.domain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.lexmint.HSClans;
import ru.lexmint.domain.io.MySQL;
import ru.lexmint.utils.Utils;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class used to interact with MySQL Database more easily.
 * It does all job connected with database by itself.
 */
public class StorageManager {

    /**
     * SQL Connection to provide IO.
     */
    private Connection connection;

    /**
     * Table prefix used to define HSClan's tables in MySQL.
     */
    private final String tablePrefix = HSClans.instance.getSettings().getString("mysql.table-prefix");

    /**
     * Constructor initializes ClanSQLManager, connects to MySQL server. Must be called before
     * running other methods in this class.
     */
    public StorageManager() {
        try {
            connection = MySQL.instance.getConnection();
            prepareDB();
        } catch (SQLException e) {
            HSClans.instance.getServer().shutdown();
            HSClans.instance.getDebug().error("SQL Error while initialization of ClanSQLManager. " + e);
        }
    }

    /**
     * When server runs into an exception while reading/writing information to SQL Database, it shutdowns to
     * prevent data loosing and problems.
     */
    private void onSQLException() {
        HSClans.instance.getServer().shutdown();
    }

    /**
     * Creates all needed tables in MySQL DB.
     */
    private void prepareDB() {
        try {
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "clans (" +
                    "name VARCHAR(8) NOT NULL, " +
                    "description VARCHAR(92), " +
                    "members VARCHAR(1000) NOT NULL, " +
                    "claims_number SMALLINT NOT NULL, " +
                    "time_created BIGINT(16) NOT NULL, " +
                    "alliances VARCHAR(26) NOT NULL, " +
                    "home_x DOUBLE, " +
                    "home_y DOUBLE, " +
                    "home_z DOUBLE, " +
                    "home_pitch FLOAT, " +
                    "home_yaw FLOAT, " +
                    "home_world VARCHAR(32), " +
                    "PRIMARY KEY (Name)" +
                    ") CHARACTER SET utf8");

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "players (" +
                    "name VARCHAR(24) NOT NULL, " +
                    "clan VARCHAR(8)," +
                    "role VARCHAR(16)," +
                    "power DOUBLE NOT NULL, " +
                    "power_boost DOUBLE NOT NULL, " +
                    "last_power_update BIGINT(16) NOT NULL, " +
                    "kills SMALLINT NOT NULL," +
                    "points MEDIUMINT NOT NULL," +
                    "deaths SMALLINT NOT NULL, " +
                    "first_played BIGINT(16) NOT NULL, " +
                    "last_played BIGINT(16) NOT NULL, " +
                    "hours_played DOUBLE NOT NULL, " +
                    "hours_played_week DOUBLE NOT NULL, " +
                    "last_played_week_update BIGINT(16) NOT NULL, " +
                    "PRIMARY KEY (Name)" +
                    ") CHARACTER SET utf8");

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "claims (" +
                    "x SMALLINT NOT NULL, " +
                    "z SMALLINT NOT NULL, " +
                    "world VARCHAR(32) NOT NULL, " +
                    "clan VARCHAR(8) NOT NULL" +
                    ") CHARACTER SET utf8");
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while preparing MySQL DB. " + e);
            onSQLException();
        }

    }


    /**
     * Get clans list.
     *
     * @param lowerCase Defines should be clans in map stored by clan name in lower case
     *                  or not.
     * @return Map which contains all clans.
     */
    public Map<String, Clan> importClans(boolean lowerCase) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement
                    ("SELECT * FROM " + tablePrefix + "clans");
            ResultSet rs = ps.executeQuery();
            Map<String, Clan> clansByName = new HashMap<>();
            Map<Clan, String[]> alliesMap = new HashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                long timeCreated = rs.getLong("time_created");
                String homeWorld = rs.getString("home_world");

                Clan clan = new Clan(name, timeCreated);
                clan.setDescription(description);
                if (homeWorld != null) {
                    double homeX = rs.getDouble("home_x");
                    double homeY = rs.getDouble("home_y");
                    double homeZ = rs.getDouble("home_z");
                    float homePitch = rs.getFloat("home_pitch");
                    float homeYaw = rs.getFloat("home_yaw");
                    Location homeLocation = new Location(Bukkit.getWorld(homeWorld), homeX, homeY, homeZ, homeYaw, homePitch);
                    clan.setHome(homeLocation);
                }

                String members = rs.getString("members");
                if (!members.isEmpty()) {
                    for (String member : members.split(",")) {
                        clan.addPlayer(member);
                    }
                }

                /** Adding clan's alliances to temporary map **/
                String alliances = rs.getString("alliances");
                if (!alliances.isEmpty()) {
                    alliesMap.put(clan, alliances.split(","));
                }

                clansByName.put(lowerCase ? clan.getName().toLowerCase() : clan.getName(), clan);
            }

            /** Initialization of alliances **/
            for (Clan clan : alliesMap.keySet()) {
                for (String alliance : alliesMap.get(clan)) {
                    clan.addAlliance(clansByName.get(lowerCase ? alliance.toLowerCase() : alliance));
                }
            }

            return clansByName;
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while importing CLANS in ClanSQLManager. " + e);
            onSQLException();
        } finally {
            closeStatement(ps);
        }
        return null;
    }

    /**
     * Imports clan claims. Notice that clan have to be already initializated and loaded to cache.
     *
     * @return List of claims. May be null when SQLException has been thrown during the execution.
     */
    public Set<Claim> importClaims() {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("SELECT * FROM " + tablePrefix + "claims");
            ResultSet rs = ps.executeQuery();
            Set<Claim> claimSet = new HashSet<>();
            while (rs.next()) {
                int x = rs.getInt("x");
                int z = rs.getInt("z");
                String clanName = rs.getString("clan");
                String world = rs.getString("world");
                Clan clan = HSClans.instance.getClanManager().getClan(clanName);
                if (clan == null) {
                    HSClans.instance.getDebug().error("Clan in method importClaims() (StorageManager) is null!");
                    continue;
                }
                Claim claim = new Claim(x, z, Bukkit.getWorld(world), clan);
                claimSet.add(claim);
            }
            return claimSet;
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while importing CLAIMS in ClanSQLManager. " + e);
            onSQLException();
        } finally {
            closeStatement(ps);
        }
        return null;
    }

    /**
     * Gets clan player from MySQL database.
     *
     * @param playerName Name of a player.
     * @return CPlayer object or null if there was a problem while connecting to MySQL.
     */
    public CPLayer importClanPlayer(String playerName) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("SELECT * FROM " + tablePrefix + "players WHERE name=?");
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String role = rs.getString("role");
                String clanName = rs.getString("clan");
                double power = rs.getDouble("power");
                double powerBoost = rs.getDouble("power_boost");
                long lastPowerUpdateTime = rs.getLong("last_power_update");
                int kills = rs.getInt("kills");
                int points = rs.getInt("points");
                int deaths = rs.getInt("deaths");
                long firstPlayed = rs.getLong("first_played");
                double hoursPlayed = rs.getDouble("hours_played");
                double hoursPlayedWeek = rs.getDouble("hours_played_week");
                long lastPlayedWeekUpdate = rs.getLong("last_played_week_update");
                long lastPlayed = rs.getLong("last_played");

                Clan clan = null;
                if (clanName != null) {
                    clan = HSClans.instance.getClanManager().getClan(clanName);
                }
                CPLayer cpLayer = new CPLayer(name, clan, ClanRole.valueOf(role), power, powerBoost, lastPowerUpdateTime, kills, points, deaths, firstPlayed, lastPlayed, hoursPlayed, hoursPlayedWeek, lastPlayedWeekUpdate);
                return cpLayer;
            }
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while getting CLAN PLAYER in ClanSQLManager. " + e);
            onSQLException();
        } finally {
            closeStatement(ps);
        }
        return null;
    }

    /**
     * Inserts new clan player to database.
     *
     * @param cpLayer CPLayer object which needs to be inserted.
     */
    public void addClanPlayer(final CPLayer cpLayer) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("INSERT INTO " + tablePrefix + "players " +
                            "(name, role, clan, points, power, power_boost, last_power_update, kills, deaths, first_played, " +
                            "hours_played, hours_played_week, last_played_week_update, last_played) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, cpLayer.getName());
                    ps.setString(2, cpLayer.getClanRole().toString());
                    if (cpLayer.getClan() != null) {
                        ps.setString(3, cpLayer.getClan().getName());
                    } else {
                        ps.setString(3, null);
                    }
                    ps.setInt(4, cpLayer.getPoints());
                    ps.setDouble(5, cpLayer.getPower());
                    ps.setDouble(6, cpLayer.getPowerBoost());
                    ps.setLong(7, cpLayer.getLastPowerUpdateTime());
                    ps.setInt(8, cpLayer.getKills());
                    ps.setInt(9, cpLayer.getDeaths());
                    ps.setLong(10, cpLayer.getFirstPlayed());
                    ps.setDouble(11, cpLayer.getHoursPlayedTotal());
                    ps.setDouble(12, cpLayer.getHoursPlayedWeek());
                    ps.setLong(13, cpLayer.getLastPlayedWeekUpdate());
                    ps.setLong(14, cpLayer.getLastPlayed());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while inserting new CLAN PLAYER in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }

    /**
     * Updates all player's info (except his name) to database.
     *
     * @param cpLayer CPLayer object which needs to be updated.
     */
    public void updateClanPlayer(final CPLayer cpLayer) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("UPDATE " + tablePrefix + "players SET " +
                            "role=?, clan=?, points=?, power=?, power_boost=?, last_power_update=?, kills=?, deaths=?, " +
                            "hours_played=?, hours_played_week=?, last_played_week_update=?, " +
                            "last_played=? WHERE name=?");
                    ps.setString(1, cpLayer.getClanRole().toString());
                    if (cpLayer.getClan() != null) {
                        ps.setString(2, cpLayer.getClan().getName());
                    } else {
                        ps.setString(2, null);
                    }
                    ps.setInt(3, cpLayer.getPoints());
                    ps.setDouble(4, cpLayer.getPower());
                    ps.setDouble(5, cpLayer.getPowerBoost());
                    ps.setLong(6, cpLayer.getLastPowerUpdateTime());
                    ps.setInt(7, cpLayer.getKills());
                    ps.setInt(8, cpLayer.getDeaths());
                    ps.setDouble(9, cpLayer.getHoursPlayedTotal());
                    ps.setDouble(10, cpLayer.getHoursPlayedWeek());
                    ps.setLong(11, cpLayer.getLastPlayedWeekUpdate());
                    ps.setLong(12, cpLayer.getLastPlayed());
                    ps.setString(13, cpLayer.getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while updating CLAN PLAYER in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }

    /**
     * Updates all clan's info (except its name) to database.
     *
     * @param clan Clan object which needs to be updated.
     */
    public void updateClan(final Clan clan) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    if (clan.getHome() != null) {
                        ps = connection.prepareStatement("UPDATE " + tablePrefix + "clans SET " +
                                "description=?, members=?, claims_number=?, alliances=?, " +
                                "home_x=?, home_y=?, home_z=?, home_pitch=?," +
                                "home_yaw=?, home_world=? WHERE name=?");

                        Location homeLocation = clan.getHome();
                        ps.setDouble(5, homeLocation.getX());
                        ps.setDouble(6, homeLocation.getY());
                        ps.setDouble(7, homeLocation.getZ());
                        ps.setFloat(8, homeLocation.getPitch());
                        ps.setFloat(9, homeLocation.getYaw());
                        ps.setString(10, homeLocation.getWorld().getName());
                        ps.setString(11, clan.getName());
                    } else {
                        ps = connection.prepareStatement("UPDATE " + tablePrefix + "clans SET " +
                                "description=?, members=?, claims_number=?, alliances=? WHERE name=?");

                        ps.setString(5, clan.getName());
                    }

                    if (clan.getDescription() != null && clan.getDescription() != HSClans.instance.getLangConfig().getString("clan.description")) {
                        ps.setString(1, clan.getDescription());
                    } else {
                        ps.setString(1, null);
                    }
                    ps.setString(2, Utils.convertToString(clan.getMembers(), false));
                    ps.setInt(3, clan.getClaimsNumber());
                    ps.setString(4, Utils.convertToString(Utils.getClanNames(clan.getAlliances()), false));

                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while updating CLAN in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }

    /**
     * Inserts new clan to database.
     *
     * @param clan Clan object which needs to be updated.
     * @return True whether insert has been successful, otherwise false.
     */
    public void addClan(final Clan clan) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("INSERT INTO " + tablePrefix + "clans " +
                            "(name, description, members, claims_number, time_created, alliances) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");

                    ps.setString(1, clan.getName());
                    ps.setString(2, clan.getDescription());
                    ps.setString(3, Utils.convertToString(clan.getMembers(), false));
                    ps.setInt(4, clan.getClaimsNumber());
                    ps.setLong(5, clan.getCreatedTime());
                    ps.setString(6, Utils.convertToString(Utils.getClanNames(clan.getAlliances()), false));
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while inserting new CLAN in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }

    public void removeClan(final Clan clan) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("DELETE FROM " + tablePrefix + "clans " +
                            "WHERE name=? ");
                    ps.setString(1, clan.getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while deleting CLAN in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }

    public void addClaim(final Claim claim) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("INSERT INTO " + tablePrefix + "claims " +
                            "(x, z, world, clan) " +
                            "VALUES (?, ?, ?, ?)");
                    ps.setInt(1, claim.getClaimLocation().getX());
                    ps.setInt(2, claim.getClaimLocation().getZ());
                    ps.setString(3, claim.getClaimLocation().getWorld().getName());
                    ps.setString(4, claim.getClan().getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while inserting new CLAIM in ClanSQLManager. " + e);
                    onSQLException();
                }
            }
        });
    }

    public void removeClaim(final Claim claim) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("DELETE FROM " + tablePrefix + "claims " +
                            "WHERE x=? AND z=? AND world=?");
                    ps.setInt(1, claim.getClaimLocation().getX());
                    ps.setInt(2, claim.getClaimLocation().getZ());
                    ps.setString(3, claim.getClaimLocation().getWorld().getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while deleting CLAN in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }

    public void updateClaimClan(final Claim claim) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("UPDATE " + tablePrefix + "claims " +
                            "SET clan=? " +
                            "WHERE x=? AND z=? AND world=?");
                    ps.setString(1, claim.getClan().getName());
                    ps.setInt(2, claim.getClaimLocation().getX());
                    ps.setInt(3, claim.getClaimLocation().getZ());
                    ps.setString(4, claim.getClaimLocation().getWorld().getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while deleting CLAN in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }

    public void removeClanPlayer(final CPLayer cpLayer) {
        MySQL.instance.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;
                try {
                    ps = connection.prepareStatement("DELETE FROM " + tablePrefix + "players " +
                            "WHERE name=? ");
                    ps.setString(1, cpLayer.getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while deleting CLAN PLAYER in ClanSQLManager. " + e);
                    onSQLException();
                } finally {
                    closeStatement(ps);
                }
            }
        });
    }


    /**
     * Safely closes the statement and ResultSet, currently associated with statement (considering
     * java docs for sql).
     *
     * @param statement Statement. May be null, method checks it.
     */
    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                HSClans.instance.getDebug().error("SQL Error. Statement can't be closed. " + e);
                onSQLException();
            }
        }
    }


}
