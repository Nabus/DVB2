package com.nabusdev.padmedvbts2.util;

public class Constants {
    public static final String IDENTIFICATION = "ident";
    public static final String DB_USER = "dbUser";
    public static final String DB_PASSWORD = "dbPassword";
    public static final String DB_HOST = "dbHost";
    public static final String DB_PORT = "dbPort";
    public static final String DB_TYPE = "dbType";
    public static final String DB_NAME = "dbName";

    public class Telnet {
        public static final int PORT = 12345;
        public static final String LAST_CONFIGURATION_READ = "lastConfigurationRead"; // for 'CONFIG STATUS' telnet com

        public class Commands {
            public static final String CHANNEL_INPUT_START = "CHANNEL INPUT START";
            public static final String CHANNEL_INPUT_STOP = "CHANNEL INPUT STOP";
            public static final String CHANNEL_INPUT_RESTART = "CHANNEL INPUT RESTART";
            public static final String CHANNEL_OUTPUT_START = "CHANNEL OUTPUT START";
            public static final String CHANNEL_OUTPUT_STOP = "CHANNEL OUTPUT STOP";
            public static final String CHANNEL_OUTPUT_RESTART = "CHANNEL OUTPUT RESTART";
            public static final String CHANNEL_RENEW = "CHANNEL RENEW";
            public static final String CHANNEL_STATUS = "CHANNEL STATUS";
            public static final String CHANNEL_ALLSTATUS = "CHANNEL ALLSTATUS";
            public static final String CONFIG_RENEW = "CONFIG RENEW";
            public static final String CONFIG_STATUS = "CONFIG STATUS";
            public static final String DBCONN_RENEW = "DBCONN RENEW";
            public static final String DBCONN_STATUS = "DBCONN STATUS";
        }
    }

    public class Table {

        public class ServerSetup {
            public static final String TABLE_NAME = "server_setup";
            public static final String IDENT = "ident";
            public static final String SETUP_KEY = "setup_key";
            public static final String SETUP_VALUE = "setup_value";

            public static final String COMMAND_MANAGER_INTERFACE = "commandManagerInterface";
            public static final String COMMAND_MANAGER_HOST = "commandManagerHost";
            public static final String COMMAND_MANAGER_PORT = "commandManagerPort";
            public static final String COMMAND_MANAGER_ALLOW_FROM = "commandManagerAllowFrom";
            public static final String DB_MANAGER_HOST = "dbManagerHost";
            public static final String DB_MANAGER_PORT = "dbManagerPort";
            public static final String DB_MANAGER_USERNAME = "dbManagerUsername";
            public static final String DB_MANAGER_PASSWORD = "dbManagerPassword";
            public static final String DB_MANAGER_DATABASE = "dbManagerDatabase";
            public static final String DB_MANAGER_PERIOD = "dbManagerPeriod";
            public static final String DB_MANAGER_TIMEOUT = "dbManagerTimeout";
        }

        public class Channels {
            public static final String TABLE_NAME = "channels";
            public static final String ADAPTER_ID = "adapter_id";
            public static final String NAME = "name";
            public static final String IDENT = "ident";
            public static final String PNR = "pnr";
            public static final String THUMB_SAVE_PATH = "thumb_save_path";
            public static final String THUMB_SAVE_FILENAME_PATTERN = "thumb_save_filename_pattern";
            public static final String THUMB_SAVE_PERIOD = "thumb_save_period";
            public static final String THUMB_SAVE_FORMAT = "thumb_save_format";
            public static final String DATE_START = "date_start";
            public static final String DATE_STOP = "date_stop";
            public static final String DATE_FAILED = "date_failed";
            public static final String FAILED_MESSAGE = "failed_message";
            public static final String ACTIVE = "active";
        }

        public class Adapters {
            public static final String TABLE_NAME = "adapters";
            public static final String IDENT = "ident";
            public static final String PATH = "path";
            public static final String ADAPTER_TYPE = "adapter_type";
            public static final String FREQUENCY = "frequency";
            public static final String BANDWIDTH = "bandwidth";
            public static final String TRANSMISSION_MODE = "transmission_mode";
            public static final String GUARD_INTERVAL = "guard_interval";
            public static final String HIERARCHY = "hierarchy";
            public static final String MODULATION = "modulation";
            public static final String DATE_START = "date_start";
            public static final String DATE_STOP = "date_stop";
            public static final String DATE_FAILED = "date_failed";
            public static final String FAILED_MESSAGE = "failed_message";
            public static final String ACTIVE = "active";
        }
    }
}
