package com.nabusdev.padmedvbts2.service.telnet;
import org.apache.mina.core.session.IoSession;

public interface TelnetCommand {
    boolean perform(String argument, IoSession session);
    String getAnswer();
}
