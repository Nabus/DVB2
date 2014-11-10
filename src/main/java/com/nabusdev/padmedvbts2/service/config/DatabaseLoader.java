package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.IDENTIFICATION;
import static com.nabusdev.padmedvbts2.util.Constants.Table.ServerSetup.*;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseLoader extends ConfigLoader {
    private static Database db = DatabaseProvider.getConfigDB();
    private static Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    public static void load() {
        String query = String.format("SELECT %s, %s FROM %s WHERE %s LIKE '%s';",
                SETUP_KEY, SETUP_VALUE, TABLE_NAME, IDENT, IDENTIFICATION);
        ResultSet resultSet = db.selectSql(query);
        Map<String, String> dbProperties = asMap(resultSet);
        for (String key : dbProperties.keySet()) {
            String value = dbProperties.get(key);
            setVariable(key, value);
        }
    }

    private static Map<String, String> asMap(ResultSet resultSet) {
        Map<String, String> map = new HashMap<>();
        try {
            while (resultSet.next()){
                String key = resultSet.getString(SETUP_KEY);
                String value = resultSet.getString(SETUP_VALUE);
                map.put(key, value);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        if (map.isEmpty()) logger.warn("Received settings from DB is empty");
        return map;
    }
}
