package com.nabusdev.padmedvbts2.service.telnet.commands;

import com.nabusdev.padmedvbts2.util.Database;

public class DBCONN extends BaseTelnetCommand {

    public boolean execute() {
        setCommandsCount(1);
        if (command.startsWith("RENEW")) return renew();
        if (command.startsWith("STATUS")) return status();
        return false;
    }

    private boolean renew() {
        Database.reconnect();
        setAnswer("Reconnection..");
        return true;
    }

    private boolean status() {
        boolean isConnected = Database.isConnected();
        if (isConnected) setAnswer("Connected");
        else setAnswer("Not connected");
        return true;
    }
}
