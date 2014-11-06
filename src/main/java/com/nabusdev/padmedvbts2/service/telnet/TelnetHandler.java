package com.nabusdev.padmedvbts2.service.telnet;
import static com.nabusdev.padmedvbts2.util.Constants.Config.*;
import com.nabusdev.padmedvbts2.util.Variable;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import javax.naming.directory.NoSuchAttributeException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class TelnetHandler extends IoHandlerAdapter {

    public void sessionOpened(IoSession session) {
        if (!isAllowedIp(session)) session.close(false);
    }

    private boolean isAllowedIp(IoSession session) {
        SocketAddress socketAddress = session.getRemoteAddress();
        InetSocketAddress inetSocket = (InetSocketAddress) socketAddress;
        String sessionAddress = inetSocket.getHostName();
        String[] allowedAddresses = Variable.get(COMMAND_MANAGER_ALLOW_FROM).split(",");
        for (String ip : allowedAddresses) {
            if (sessionAddress.equals(ip)) {
                return true;
            }
        }
        return false;
    }

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
