package ru.lexmint.domain.io;

import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class which controls all clans of the server, loads them, etc.
 * It stores clans using MySQL.
 */
public class ClanSQLStorage implements ClanManager {
    private List<Clan> clansList = new ArrayList<Clan>();

    @Override
    public void loadClans() {
        try {
            Connection connection = SQLManager.instance.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                String tag = resultSet.getString("Tag");
                String members = resultSet.getString("Members");

                Clan clan = new Clan(name, tag);
            }
        } catch (SQLException e) {
            HSClans.instance.getLogger().severe("Error while loading clans. " + e.getMessage());
        }
    }

    @Override
    public CPLayer getPlayer(String playerName) {
        return null;
    }

    @Override
    public void removePlayer(CPLayer player) {

    }

    @Override
    public void addPlayer(CPLayer player) {

    }
}
