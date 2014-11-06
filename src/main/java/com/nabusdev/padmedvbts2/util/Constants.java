package com.nabusdev.padmedvbts2.util;

public class Constants {
    public static final String DB_USER = Variable.get("dbPort");
    public static final String DB_PASSWORD = Variable.get("dbPassword");
    public static final String DB_IP = Variable.get("dbIp");
    public static final String DB_PORT = Variable.get("dbPort");
    public static final String DB_TYPE = Variable.get("dbType");
    public static final String DB_TABLE = Variable.get("dbTable");
    public static final String CONNECTION_LINK = "jdbc:" + DB_TYPE + "://" + DB_IP + ":" + DB_PORT + "/" + DB_TABLE;
    public static final String IDENTIFICATION = Variable.get("ident");

    public static final String DB_MANAGER_CONNECTION_LINK = "jdbc:" + DB_TYPE + "://" +
            Config.DB_MANAGER_HOST + ":" + Config.DB_MANAGER_PORT + "/" + Config.DB_MANAGER_DATABASE;

    public class Config {
        public static final String LAST_CONFIGURATION_READ = "lastConfigurationRead";

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

    public class Telnet {
        public static final int PORT = 12345;

        public class Command {

        }
    }

    public class Table {
        public class ServerSetup {
            public static final String IDENT = "ident";
            public static final String SETUP_KEY = "setup_key";
            public static final String SETUP_VALUE = "setup_value";
        }
    }
}
