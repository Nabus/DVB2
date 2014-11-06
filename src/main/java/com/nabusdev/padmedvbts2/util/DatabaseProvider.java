package com.nabusdev.padmedvbts2.util;
import static com.nabusdev.padmedvbts2.util.Constants.*;
import static com.nabusdev.padmedvbts2.util.Constants.Config.*;

public class DatabaseProvider {
    public static final Database configDB = new Database(CONNECTION_LINK, DB_USER, DB_PASSWORD);
    public static final Database channelsDB = new Database(DB_MANAGER_CONNECTION_LINK, DB_MANAGER_USERNAME, DB_MANAGER_PASSWORD);
}
