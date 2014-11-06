package com.nabusdev.padmedvbts2.service.telnet.commands;

public class CHANNEL extends BaseTelnetCommand {

    public boolean execute() {
        setCommandsCount(2);
        if (command.startsWith("INPUT START")) return inputStart();
        if (command.startsWith("INPUT STOP")) return inputStop();
        if (command.startsWith("INPUT RESTART")) return inputRestart();
        if (command.startsWith("OUTPUT START")) return outputStart();
        if (command.startsWith("OUTPUT STOP")) return outputStop();
        if (command.startsWith("OUTPUT RESTART")) return outputRestart();
        setCommandsCount(1);
        if (command.startsWith("RENEW")) return renew();
        if (command.startsWith("STATUS")) return status();
        if (command.startsWith("ALLSTATUS")) return allstatus();
        return false;
    }

    private boolean inputStart() {
        // TODO
        return true;
    }

    private boolean inputStop() {
        // TODO
        return true;
    }

    private boolean inputRestart() {
        // TODO
        return true;
    }

    private boolean outputStart() {
        // TODO
        return true;
    }

    private boolean outputStop() {
        // TODO
        return true;
    }

    private boolean outputRestart() {
        return (outputStart() && outputStop());
    }

    private boolean renew() {
        // TODO
        return true;
    }

    private boolean status() {
        // TODO
        return true;
    }

    private boolean allstatus() {
        // TODO
        return true;
    }
}
