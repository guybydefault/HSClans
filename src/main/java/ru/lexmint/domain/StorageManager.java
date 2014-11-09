package ru.lexmint.domain;

import com.sun.javafx.beans.annotations.NonNull;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.io.MySQL;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

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
    private final String tablePrefix = HSClans.instance.settings.getString("mysql-table-prefix");

    /**
     * Constructor initializes ClanSQLManager, connects to MySQL server. Must be called before
     * running other methods in this class.
     */
    public void StorageManager() {
        try {
            connection = MySQL.instance.getConnection();
            prepareDB();
        } catch (SQLException e) {
            HSClans.instance.debug.error("SQL Error while initialization of ClanSQLManager. " + e);
        }
    }

    /**
     * Creates all needed tables in MySQL DB.
     */
    private void prepareDB() {
        try {

            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "clans (" +
                    "Name VARCHAR(16) NOT NULL, " +
                    "Description VARCHAR(96) NOT NULL, " +
                    "Members VARCHAR(1000) NOT NULL, " +
                    "PRIMARY KEY (Name)" +
                    ") CHARACTER SET utf8");

            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "players (" +
                    "Name VARCHAR(24) NOT NULL, " +
                    "Clan VARCHAR(16)," +
                    "Role VARCHAR(16)," +
                    "PRIMARY KEY (Name)" +
                    ") CHARACTER SET utf8");
        } catch (SQLException e) {
            HSClans.instance.debug.error("SQL Error while preparing MySQL DB. " + e);
        }

    }


    /**
     * Get clans list.
     *
     * @return List containing all clans from MySQL table.
     */
    public List<Clan> importClans() {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement
                    ("SELECT * FROM " + tablePrefix + "clans");
            ResultSet rs = ps.executeQuery();

            List<Clan> clanList = new LinkedList<>();
            while (rs.next()) {
                String name = rs.getString("Name");
                String description = rs.getString("Description");

                Clan clan = new Clan(name);
                clan.setDescription(description);

                String members = rs.getString("Members");
                for (String member : members.split(",")) {
                    CPLayer cpLayer = HSClans.instance.clanManager.getPlayer(member);
                    // TODO CPlayer must not be null. I need to check clanmanager!
                    if (cpLayer != null) {
                        clan.addPlayer(cpLayer);
                    }
                }
                clanList.add(clan);
            }
        } catch (SQLException e) {
            HSClans.instance.debug.error("SQL Error while getting clans in ClanSQLManager. " + e);
        } finally {
            closeStatement(ps);
            return null;
        }
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
            ps = connection.prepareStatement("SELECT * FROM " + tablePrefix + "players WHERE Name='?'");
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("Name");
                String role = rs.getString("Role");
                String clanName = rs.getString("Clan");
                Clan clan = HSClans.instance.clanManager.getClan(clanName);

                CPLayer cpLayer = new CPLayer(name, clan, CPLayer.ClanRole.valueOf(role));
                return cpLayer;
            }
        } catch (SQLException e) {
            HSClans.instance.debug.error("SQL Error while getting clan player in ClanSQLManager. " + e);
        } finally {
            closeStatement(ps);
            return null;
        }
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
                HSClans.instance.debug.error("SQL Error. Statement can't be closed. " + e);
            }
        }
    }


}
