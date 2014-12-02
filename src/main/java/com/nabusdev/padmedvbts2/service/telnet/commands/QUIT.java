package com.nabusdev.padmedvbts2.service.telnet.commands;
import static com.nabusdev.padmedvbts2.util.Constants.Telnet.Commands.QUIT;
import com.nabusdev.padmedvbts2.service.telnet.CommandsCase;
import com.nabusdev.padmedvbts2.service.telnet.TelnetCommand;
import org.apache.mina.core.session.IoSession;

public class Quit implements CommandsCase {

    @TelnetCommand(QUIT)
    private void execute(IoSession session) {
        session.close(false);
    }
}
