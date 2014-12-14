package ru.lexmint.domain;

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
                    "Name VARCHAR(8) NOT NULL, " +
                    "Description VARCHAR(96), " +
                    "Members VARCHAR(1000) NOT NULL, " +
                    "League VARCHAR(16) NOT NULL, " +
                    "ClaimsNumber SMALLINT NOT NULL, " +
                    "PRIMARY KEY (Name)" +
                    ") CHARACTER SET utf8");

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "players (" +
                    "Name VARCHAR(24) NOT NULL, " +
                    "Clan VARCHAR(8)," +
                    "Role VARCHAR(16)," +
                    "League VARCHAR(16) NOT NULL, " +
                    "Power REAL NOT NULL, " +
                    "PowerBoost REAL NOT NULL, " +
                    "LastPowerUpdate BIGINT(16) NOT NULL, " +
                    "PRIMARY KEY (Name)" +
                    ") CHARACTER SET utf8");

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "claims (" +
                    "X SMALLINT NOT NULL, " +
                    "Z SMALLINT NOT NULL, " +
                    "Clan VARCHAR(8) NOT NULL" +
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
                String name = rs.getString("Name");
                String description = rs.getString("Description");
                ClanLeague clanLeague = ClanLeague.valueOf(rs.getString("League"));

                Clan clan = new Clan(name, clanLeague);
                clan.setDescription(description);

                String members = rs.getString("Members");
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
                int x = rs.getInt("X");
                int z = rs.getInt("Z");
                String clanName = rs.getString("Clan");
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
            ps = connection.prepareStatement("SELECT * FROM " + tablePrefix + "players WHERE Name=?");
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("Name");
                String role = rs.getString("Role");
                String clanName = rs.getString("Clan");
                String clanLeague = rs.getString("League");
                double power = rs.getDouble("Power");
                double powerBoost = rs.getDouble("PowerBoost");
                long lastPowerUpdateTime = rs.getLong("LastPowerUpdate");

                Clan clan = HSClans.instance.getClanManager().getClan(clanName);
                CPLayer cpLayer = new CPLayer(name, clan, ClanRole.valueOf(role), ClanLeague.valueOf(clanLeague), power, powerBoost, lastPowerUpdateTime);
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
                            "(Name, Role, Clan, League, Power, PowerBoost, LastPowerUpdate) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
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
                            "Role=?, Clan=?, League=?, Power=?, PowerBoost=?, LastPowerUpdate=? WHERE Name=?");
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
                    ps.setString(7, cpLayer.getName());
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
                    ps = connection.prepareStatement("UPDATE " + tablePrefix + "clans SET " +
                            "Description=?, Members=?, League=?, ClaimsNumber=? WHERE Name=?");
                    if (clan.getDescription() != null) {
                        ps.setString(1, clan.getDescription());
                    } else {
                        ps.setString(1, null);
                    }
                    ps.setString(2, Utils.convertToString(clan.getMembers(), false));
                    ps.setString(3, clan.getClanLeague().toString());
                    ps.setInt(4, clan.getClaimsNumber());
                    ps.setString(5, clan.getName());
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
                            "(Name, Description, Members, League, ClaimsNumber) " +
                            "VALUES (?, ?, ?, ?, ?)");

                    ps.setString(1, clan.getName());
                    ps.setString(2, clan.getDescription());
                    ps.setString(3, Utils.convertToString(clan.getMembers(), false));
                    ps.setString(4, clan.getClanLeague().toString());
                    ps.setInt(5, clan.getClaimsNumber());
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
                            "WHERE Name=? ");
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
                            "(X, Z, Clan) " +
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
                            "WHERE X=? AND Z=?");
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
                            "SET Clan=? " +
                            "WHERE X=? AND Z=?");
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
                            "WHERE Name=? ");
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
