package com.nabusdev.padmedvbts2.service.telnet.commands;

public class CONFIG extends BaseTelnetCommand {

    public boolean execute() {
        setCommandsCount(1);
        if (command.startsWith("RENEW")) return renew();
        if (command.startsWith("STATUS")) return status();
        return false;
    }

    private boolean renew() {
        // TODO
        return true;
    }

    private boolean status() {
        // TODO
        return true;
    }
}
