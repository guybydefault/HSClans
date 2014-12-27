package ru.lexmint.domain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.lexmint.HSClans;
import ru.lexmint.domain.io.MySQL;
import ru.lexmint.utils.Utils;

import java.sql.*;
import java.util.HashSet;
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
            HSClans.instance.getDebug().error("SQL Error while initialization of ClanSQLManager. " + e);
        }
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
                    "league VARCHAR(16) NOT NULL, " +
                    "power DOUBLE NOT NULL, " +
                    "power_boost DOUBLE NOT NULL, " +
                    "last_power_update BIGINT(16) NOT NULL, " +
                    "kills SMALLINT NOT NULL," +
                    "deaths SMALLINT NOT NULL, " +
                    "first_played BIGINT(16) NOT NULL, " +
                    "hours_played DOUBLE NOT NULL, " +
                    "PRIMARY KEY (Name)" +
                    ") CHARACTER SET utf8");

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "claims (" +
                    "x SMALLINT NOT NULL, " +
                    "z SMALLINT NOT NULL, " +
                    "clan VARCHAR(8) NOT NULL" +
                    ") CHARACTER SET utf8");
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while preparing MySQL DB. " + e);
        }

    }


    /**
     * Get clans list.
     *
     * @return List containing all clans from MySQL table.
     */
    public Set<Clan> importClans() {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement
                    ("SELECT * FROM " + tablePrefix + "clans");
            ResultSet rs = ps.executeQuery();
            Set<Clan> clanSet = new HashSet<>();
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
                clanSet.add(clan);
            }
            return clanSet;
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while importing CLANS in ClanSQLManager. " + e);
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
                Clan clan = HSClans.instance.getClanManager().getClan(clanName);
                if (clan == null) {
                    HSClans.instance.getDebug().error("Clan in method importClaims() (StorageManager) is null!");
                    continue;
                }
                Claim claim = new Claim(x, z, clan);
                claimSet.add(claim);
            }
            return claimSet;
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while importing CLAIMS in ClanSQLManager. " + e);
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
                String clanLeague = rs.getString("league");
                double power = rs.getDouble("power");
                double powerBoost = rs.getDouble("power_boost");
                long lastPowerUpdateTime = rs.getLong("last_power_update");
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                long firstPlayed = rs.getLong("first_played");
                double hoursPlayed = rs.getDouble("hours_played");

                Clan clan = HSClans.instance.getClanManager().getClan(clanName);
                CPLayer cpLayer = new CPLayer(name, clan, ClanRole.valueOf(role), ClanLeague.valueOf(clanLeague), power, powerBoost, lastPowerUpdateTime, kills, deaths, firstPlayed, hoursPlayed);
                return cpLayer;
            }
        } catch (SQLException e) {
            HSClans.instance.getDebug().error("SQL Error while getting CLAN PLAYER in ClanSQLManager. " + e);
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
                            "(name, role, clan, league, power, power_boost, last_power_update, kills, deaths, first_played, " +
                            "hours_played) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, cpLayer.getName());
                    ps.setString(2, cpLayer.getClanRole().toString());
                    if (cpLayer.getClan() != null) {
                        ps.setString(3, cpLayer.getClan().getName());
                    } else {
                        ps.setString(3, null);
                    }
                    ps.setString(4, cpLayer.getClanLeague().toString());
                    ps.setDouble(5, cpLayer.getPower());
                    ps.setDouble(6, cpLayer.getPowerBoost());
                    ps.setLong(7, cpLayer.getLastPowerUpdateTime());
                    ps.setInt(8, cpLayer.getStats().getKills());
                    ps.setInt(9, cpLayer.getStats().getDeaths());
                    ps.setLong(10, cpLayer.getFirstPlayed());
                    ps.setDouble(11, cpLayer.getHoursPlayed());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while inserting new CLAN PLAYER in ClanSQLManager. " + e);
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
                            "role=?, clan=?, league=?, power=?, power_boost=?, last_power_update=?, kills=?, deaths=?, " +
                            "hours_played=? WHERE name=?");
                    ps.setString(1, cpLayer.getClanRole().toString());
                    if (cpLayer.getClan() != null) {
                        ps.setString(2, cpLayer.getClan().getName());
                    } else {
                        ps.setString(2, null);
                    }
                    ps.setString(3, cpLayer.getClanLeague().toString());
                    ps.setDouble(4, cpLayer.getPower());
                    ps.setDouble(5, cpLayer.getPowerBoost());
                    ps.setLong(6, cpLayer.getLastPowerUpdateTime());
                    ps.setInt(7, cpLayer.getStats().getKills());
                    ps.setInt(8, cpLayer.getStats().getDeaths());
                    ps.setDouble(9, cpLayer.getHoursPlayed());

                    ps.setString(10, cpLayer.getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while updating CLAN PLAYER in ClanSQLManager. " + e);
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
                                "description=?, members=?, league=?, claims_number=?, home_x=?, home_y=?, home_z=?, home_pitch=?," +
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
                                "description=?, members=?, claims_number=? WHERE name=?");

                        ps.setString(5, clan.getName());
                    }

                    if (clan.getDescription() != null && clan.getDescription() != HSClans.instance.getLangConfig().getString("clan.description")) {
                        ps.setString(1, clan.getDescription());
                    } else {
                        ps.setString(1, null);
                    }
                    ps.setString(2, Utils.convertToString(clan.getMembers(), false));
                    ps.setInt(3, clan.getClaimsNumber());

                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while updating CLAN in ClanSQLManager. " + e);
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
                            "(name, description, members, claims_number, time_created) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");

                    ps.setString(1, clan.getName());
                    ps.setString(2, clan.getDescription());
                    ps.setString(3, Utils.convertToString(clan.getMembers(), false));
                    ps.setInt(4, clan.getClaimsNumber());
                    ps.setLong(5, clan.getCreatedTime());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while inserting new CLAN in ClanSQLManager. " + e);
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
                            "(x, z, clan) " +
                            "VALUES (?, ?, ?)");
                    ps.setInt(1, claim.getClaimLocation().getX());
                    ps.setInt(2, claim.getClaimLocation().getZ());
                    ps.setString(3, claim.getClan().getName());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while inserting new CLAIM in ClanSQLManager. " + e);
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
                            "WHERE x=? AND z=?");
                    ps.setInt(1, claim.getClaimLocation().getX());
                    ps.setInt(2, claim.getClaimLocation().getZ());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while deleting CLAN in ClanSQLManager. " + e);
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
                            "WHERE x=? AND z=?");
                    ps.setString(1, claim.getClan().getName());
                    ps.setInt(2, claim.getClaimLocation().getX());
                    ps.setInt(3, claim.getClaimLocation().getZ());
                    ps.execute();
                    connection.commit();
                } catch (SQLException e) {
                    HSClans.instance.getDebug().error("SQL Error while deleting CLAN in ClanSQLManager. " + e);
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
            }
        }
    }


}
