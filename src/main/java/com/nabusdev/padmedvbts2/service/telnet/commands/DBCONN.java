package com.nabusdev.padmedvbts2.service.telnet.commands;
import static com.nabusdev.padmedvbts2.util.Constants.Telnet.Commands.*;
import com.nabusdev.padmedvbts2.service.telnet.CommandsCase;
import com.nabusdev.padmedvbts2.service.telnet.TelnetCommand;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import org.apache.mina.core.session.IoSession;

public class DbConn implements CommandsCase {

    @TelnetCommand(DBCONN_RENEW)
    private void renew(IoSession session) {
        DatabaseProvider.getConfigDB().reconnect();
        session.setAttribute("answer", "Reconnection..");
    }

    @TelnetCommand(DBCONN_STATUS)
    private void status(IoSession session) {
        boolean isConnected = DatabaseProvider.getConfigDB().isConnected();
        if (isConnected) session.setAttribute("answer", "Connected");
        else session.setAttribute("answer", "Not connected");
    }
}
