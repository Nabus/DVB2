package com.nabusdev.padmedvbts2.service.telnet.commands;
import static com.nabusdev.padmedvbts2.util.Constants.Telnet.Commands.*;
import static com.nabusdev.padmedvbts2.util.Constants.Telnet.*;
import com.nabusdev.padmedvbts2.service.config.DatabaseLoader;
import com.nabusdev.padmedvbts2.service.telnet.CommandsCase;
import com.nabusdev.padmedvbts2.service.telnet.TelnetCommand;
import com.nabusdev.padmedvbts2.util.Variable;
import org.apache.mina.core.session.IoSession;

public class Config implements CommandsCase {

    @TelnetCommand(CONFIG_RENEW)
    private void renew(IoSession session) {
        DatabaseLoader.load();
    }

    @TelnetCommand(CONFIG_STATUS)
    private void status(IoSession session) {
        String lastReadTimestamp = Variable.get(LAST_CONFIGURATION_READ);
        session.setAttribute("answer", lastReadTimestamp);
    }
}
