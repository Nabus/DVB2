package com.nabusdev.padmedvbts2.util;
import static com.nabusdev.padmedvbts2.util.Constants.*;
import static com.nabusdev.padmedvbts2.util.Variable.get;

public class DatabaseProvider {
    private static volatile Database configDB;
    private static volatile Database channelsDB;

    public static Database getConfigDB() {
        synchronized (DatabaseProvider.class) {
            if (configDB == null) {
                configDB  = new Database(
                        get(Constants.Config.DB_HOST),
                        get(Constants.Config.DB_PORT),
                        get(Constants.Config.DB_NAME),
                        get(Constants.Config.DB_USER),
                        get(Constants.Config.DB_PASSWORD));
            }
        }
        return configDB;
    }

    public static Database getChannelsDB() {
        synchronized (DatabaseProvider.class) {
            if (channelsDB == null) {
                channelsDB = new Database(
                        get(Constants.Variables.DB_MANAGER_HOST),
                        get(Constants.Variables.DB_MANAGER_PORT),
                        get(Constants.Variables.DB_MANAGER_DATABASE),
                        get(Constants.Variables.DB_MANAGER_USERNAME),
                        get(Constants.Variables.DB_MANAGER_PASSWORD));
            }
        }
        return channelsDB;
    }
}
