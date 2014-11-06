package com.nabusdev.padmedvbts2.service.telnet.commands;

public class QUIT extends BaseTelnetCommand {

    public boolean execute() {
        session.close(false);
        return true;
    }
}
