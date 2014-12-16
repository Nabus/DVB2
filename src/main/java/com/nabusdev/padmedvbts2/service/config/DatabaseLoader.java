package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.*;
import com.nabusdev.padmedvbts2.util.Constants.Table.Servers;
import com.nabusdev.padmedvbts2.util.Constants.Table.ServerSetup;
import com.nabusdev.padmedvbts2.util.Constants;

import com.nabusdev.padmedvbts2.util.Constants.Config;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import com.nabusdev.padmedvbts2.util.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseLoader extends ConfigLoader {
    private static final Integer serverId = getServerId();
    private static Database db = DatabaseProvider.getConfigDB();
    private static Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    public static void load() {
        Variable.set(LAST_CONFIGURATION_READ, String.valueOf(new Date().getTime()));
        String query = String.format("SELECT %s, %s FROM %s WHERE %s LIKE '%s' AND %s = 1;",
                ServerSetup.SETUP_KEY, ServerSetup.SETUP_VALUE, ServerSetup.TABLE_NAME,
                ServerSetup.SERVER_ID, getServerId(), ServerSetup.ACTIVE);
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
            while (resultSet.next()) {
                String key = resultSet.getString(ServerSetup.SETUP_KEY);
                String value = resultSet.getString(ServerSetup.SETUP_VALUE);
                map.put(key, value);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        if (map.isEmpty()) logger.warn("Received settings from DB is empty");
        return map;
    }

    public static Integer getServerId() {
        if (serverId == null) {
            try {
                final String CONFIG_IDENT = Variable.get(Config.IDENTIFICATION);
                String query = String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s = 1",
                        Servers.ID, Servers.TABLE_NAME, Servers.IDENT, CONFIG_IDENT, Servers.ACTIVE);
                ResultSet resultSet = db.selectSql(query);
                while (resultSet.next()) {
                    return resultSet.getInt(Servers.ID);
                }
            } catch (Exception e) {
                logger.error("Failed to find instance server ID. Terminating");
                e.printStackTrace();
                System.exit(1);
            }
        }
        return serverId;
    }
}
