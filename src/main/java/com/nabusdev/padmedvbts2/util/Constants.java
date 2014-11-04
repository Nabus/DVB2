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
