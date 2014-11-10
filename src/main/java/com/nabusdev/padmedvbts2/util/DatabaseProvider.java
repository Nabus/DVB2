package com.nabusdev.padmedvbts2.util;
import static com.nabusdev.padmedvbts2.util.Constants.*;
import static com.nabusdev.padmedvbts2.util.Variable.get;

public class DatabaseProvider {
    private static volatile Database configDB;
    private static volatile Database channelsDB;

    public static Database getConfigDB() {
        Database localInstance = configDB;
        if (localInstance == null) {
            synchronized (DatabaseProvider.class) {
                localInstance = configDB;
                if (localInstance == null) {
                    configDB = localInstance = new Database(DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
                }
            }
        }
        return localInstance;
    }

    public static Database getChannelsDB() {
        Database localInstance = channelsDB;
        if (localInstance == null) {
            synchronized (DatabaseProvider.class) {
                localInstance = channelsDB;
                if (localInstance == null) {
                    channelsDB = localInstance = new Database(
                            get(Constants.Table.ServerSetup.DB_MANAGER_HOST),
                            get(Constants.Table.ServerSetup.DB_MANAGER_PORT),
                            get(Constants.Table.ServerSetup.DB_MANAGER_DATABASE),
                            get(Constants.Table.ServerSetup.DB_MANAGER_USERNAME),
                            get(Constants.Table.ServerSetup.DB_MANAGER_PASSWORD));
                }
            }
        }
        return localInstance;
    }
}
