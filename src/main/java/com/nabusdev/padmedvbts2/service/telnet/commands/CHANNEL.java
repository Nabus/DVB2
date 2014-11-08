package com.nabusdev.padmedvbts2.service.telnet.commands;
import com.nabusdev.padmedvbts2.service.telnet.CommandsCase;
import com.nabusdev.padmedvbts2.service.telnet.TelnetCommand;
import org.apache.mina.core.session.IoSession;

public class Channel implements CommandsCase {

    @TelnetCommand("CHANNEL INPUT START")
    private void inputStart(IoSession session) {
        // TODO
    }

    @TelnetCommand("CHANNEL INPUT STOP")
    private void inputStop(IoSession session) {
        // TODO
    }

    @TelnetCommand("CHANNEL INPUT RESTART")
    private void inputRestart(IoSession session) {
        inputStart(session);
        inputStop(session);
    }

    @TelnetCommand("CHANNEL OUTPUT START")
    private void outputStart(IoSession session) {
        // TODO
    }

    @TelnetCommand("CHANNEL OUTPUT STOP")
    private void outputStop(IoSession session) {
        // TODO
    }

    @TelnetCommand("CHANNEL OUTPUT RESTART")
    private void outputRestart(IoSession session) {
        outputStart(session);
        outputStop(session);
    }

    @TelnetCommand("CHANNEL RENEW")
    private void renew(IoSession session) {
        // TODO
    }

    @TelnetCommand("CHANNEL STATUS")
    private void status(IoSession session) {
        // TODO
    }

    @TelnetCommand("CHANNEL ALLSTATUS")
    private void allstatus(IoSession session) {
        // TODO
    }
}
