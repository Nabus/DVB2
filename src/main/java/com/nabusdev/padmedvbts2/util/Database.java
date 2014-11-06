package com.nabusdev.padmedvbts2.util;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.sql.*;

public class Database {
    private String connectionLink;
    private String dbUser;
    private String dbPassword;
    private Connection connection;
    private Logger logger = LoggerFactory.getLogger(Database.class);

    Database(String connectionLink, String dbUser, String dbPassword) {
        this.connectionLink = connectionLink;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public void connect(){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(connectionLink, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Statement getStatement() {
        try {
            if (connection.isClosed()) connect();
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.error("Failed to create statement for query");
        return null;
    }

    public ResultSet selectSql(String query) {
        try { return getStatement().executeQuery(query); }
        catch (SQLException e) { e.printStackTrace(); }
        logger.error("Failed to receive result from query '" + query + "'");
        return null;
    }

    public void resultSql(String query) {
        try { getStatement().executeUpdate(query); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean isConnected() {
        try {
            return (connection.isClosed() == false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void reconnect() {
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
