package com.nabusdev.padmedvbts2.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.nabusdev.padmedvbts2.util.Constants.*;
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
}
