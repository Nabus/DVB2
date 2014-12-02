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
        int channelId = (int) session.getAttribute("argument");
        com.nabusdev.padmedvbts2.model.Channel channel = com.nabusdev.padmedvbts2.model.Channel.getById(channelId);
        String status = channel.getStatus().name();
        session.setAttribute("answer", status);
    }

    @TelnetCommand(CHANNEL_ALLSTATUS)
    private void allstatus(IoSession session) {
        StringBuffer table = new StringBuffer();
        table.append(String.format("%-5s %-5s %s%n", "ID", "Adapter", "Status"));
        table.append("------------------------------------\n");
        for (com.nabusdev.padmedvbts2.model.Channel channel : com.nabusdev.padmedvbts2.model.Channel.getChannelList()) {
            int channelId = channel.getId();
            int adapterId = channel.getAdapter().getId();
            String status = channel.getStatus().name();
            table.append(String.format("%-5d %-5d %s%n", channelId, adapterId, status));
        }
        session.setAttribute("answer", table.toString());
    }
}
