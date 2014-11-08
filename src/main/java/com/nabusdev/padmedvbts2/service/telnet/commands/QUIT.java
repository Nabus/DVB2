package com.nabusdev.padmedvbts2.service.telnet.commands;
import com.nabusdev.padmedvbts2.service.telnet.CommandsCase;
import org.apache.mina.core.session.IoSession;

public class Quit implements CommandsCase {

    public void execute(IoSession session) {
        session.close(false);
    }
}
