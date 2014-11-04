package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.*;
import java.sql.*;

public class Database {

    public static void init(){
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(CONNECTION_LINK, DB_USER, DB_PASSWORD);
            Statement statement = connection.createStatement();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet selectSql(String query) {
        // TODO
        return null;
    }
}
