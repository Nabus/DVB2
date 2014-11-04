package com.nabusdev.padmedvbts2.service.telnet;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import javax.naming.directory.NoSuchAttributeException;

public class TelnetHandler extends IoHandlerAdapter {

    public void messageReceived(IoSession session, Object message) {
        String stringMessage = (String) message;
        String command = stringMessage.split(" ")[0].toLowerCase();
        try {
            Object commandClass = Class.forName("com.nabusdev.padmedvbts2.service.telnet." + command).newInstance();
            TelnetCommand telnetCommand = (TelnetCommand) commandClass;
            final int WHITESPACE = 1;
            String argument = stringMessage.substring(0, command.length() + WHITESPACE);
            final boolean isExecuted = telnetCommand.perform(argument, session);
            if (!isExecuted){
                String answer = telnetCommand.getAnswer();
                boolean gotAnswer = (answer != null);
                if (gotAnswer) session.write(answer);
                else throw new NoSuchAttributeException();
            }
        }
        catch (Exception e) {
            unknownCommandReceived(session, stringMessage);
        }
    }

    private void unknownCommandReceived(IoSession session, String message) {
        session.write("Unknown command '" + message + "'");
    }
}
