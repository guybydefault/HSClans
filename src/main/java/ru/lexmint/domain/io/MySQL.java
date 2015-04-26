package ru.lexmint.domain.io;

import ru.lexmint.HSClans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class which responses for managing sql connection and providing plugin with it.
 */
public class MySQL {
    private Connection connection = null;
    private ExecutorService executor = null;

    public static final MySQL instance = new MySQL();

    /**
     * Connect to SQL server.
     *
     * @return True if connection was successful, otherwise False.
     */
    private boolean connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://" + HSClans.instance.getConfig().getString("mysql.host") + '/' + HSClans.instance.getConfig().getString("mysql.database");
            String user = HSClans.instance.getConfig().getString("mysql.user");
            String password = HSClans.instance.getConfig().getString("mysql.password");

            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "utf-8");

            connection = DriverManager.getConnection(url, properties);
            connection.setAutoCommit(false);

            return true;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            HSClans.instance.getDebug().error("Error while connecting to MySQL server. " + e.getMessage());
        }
        return false;
    }


    /**
     * Get MySQL connection. If it is not set, it connects with MySQL and returns ready connection.
     *
     * @return Connection.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connect();
            executor = Executors.newSingleThreadExecutor();
        } else if (connection.isClosed()) {
            connect();
        }
        return connection;
    }

    /**
     * Get ExecutorService for executing SQL queries.
     *
     * @return Executor for executing SQL queries.
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Disconnects from MySQL server. This method will run in single threaded ExecutorService, which guarantees that
     * all previous operations with database will be finished and only then connection will close.
     */
    public void disconnect() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    connection.close();
                } catch (SQLException e) {

                    HSClans.instance.getDebug().error("Error while disconnecting from MySQL. " + e);
                }
            }
        });
    }
}
