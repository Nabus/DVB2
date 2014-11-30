package com.nabusdev.padmedvbts2.service.telnet.commands;
import static com.nabusdev.padmedvbts2.util.Constants.Telnet.Commands.*;
import com.nabusdev.padmedvbts2.service.StreamInput;
import com.nabusdev.padmedvbts2.service.StreamOutput;
import com.nabusdev.padmedvbts2.service.telnet.CommandsCase;
import com.nabusdev.padmedvbts2.service.telnet.TelnetCommand;
import org.apache.mina.core.session.IoSession;

public class Channel implements CommandsCase {

    @TelnetCommand(CHANNEL_INPUT_START)
    private void inputStart(IoSession session) {
        StreamInput.start();
    }

    @TelnetCommand(CHANNEL_INPUT_STOP)
    private void inputStop(IoSession session) {
        StreamInput.stop();
    }

    @TelnetCommand(CHANNEL_INPUT_RESTART)
    private void inputRestart(IoSession session) {
        inputStop(session);
        inputStart(session);
    }

    @TelnetCommand(CHANNEL_OUTPUT_START)
    private void outputStart(IoSession session) {
        StreamOutput.start();
    }

    @TelnetCommand(CHANNEL_OUTPUT_STOP)
    private void outputStop(IoSession session) {
        StreamOutput.stop();
    }

    @TelnetCommand(CHANNEL_OUTPUT_RESTART)
    private void outputRestart(IoSession session) {
        outputStop(session);
        outputStart(session);
    }

    @TelnetCommand(CHANNEL_RENEW)
    private void renew(IoSession session) {
        // TODO
    }

    @TelnetCommand(CHANNEL_STATUS)
    private void status(IoSession session) {
        // TODO
    }

    @TelnetCommand(CHANNEL_ALLSTATUS)
    private void allstatus(IoSession session) {
        // TODO
    }
}
