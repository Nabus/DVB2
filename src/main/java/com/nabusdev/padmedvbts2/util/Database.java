package com.nabusdev.padmedvbts2.util;
import static com.nabusdev.padmedvbts2.util.Constants.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.sql.*;

public class Database {
    private static Connection connection;
    private static Logger logger = LoggerFactory.getLogger(Database.class);

    public static void connect(){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(CONNECTION_LINK, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Statement getStatement() {
        try {
            if (connection.isClosed()) connect();
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.error("Failed to create statement for query");
        return null;
    }

    public static ResultSet selectSql(String query) {
        try { return getStatement().executeQuery(query); }
        catch (SQLException e) { e.printStackTrace(); }
        logger.error("Failed to receive result from query '" + query + "'");
        return null;
    }

    public static void resultSql(String query) {
        try { getStatement().executeUpdate(query); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean isConnected() {
        try {
            return (connection.isClosed() == false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void reconnect() {
        try{
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connect();
    }
}
