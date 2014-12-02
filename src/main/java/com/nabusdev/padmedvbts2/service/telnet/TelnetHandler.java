package com.nabusdev.padmedvbts2.service.telnet;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.*;
import com.nabusdev.padmedvbts2.util.Variable;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
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
        String parseResult[] = parseMessage(message);
        final int COMMAND = 0, ARGUMENT = 1;
        session.setAttribute("command", parseResult[COMMAND]);
        session.setAttribute("argument", parseResult[ARGUMENT]);
        MethodCollector.execute(session);
        String answer = (String) session.getAttribute("answer");
        if (answer != null){
            session.write(answer);
            return;
        }
        boolean isUnknownCommand = (boolean) session.getAttribute("isUnknownCommand");
        if (isUnknownCommand) {
            session.write("Unknown command '" + message + "'");
        }
    }

    private String[] parseMessage(Object message) {
        String stringMessage = (String) message;
        String[] messageSplit = stringMessage.split(" ");
        String command;
        String argument = "";
        if (messageSplit.length > 1) {
            final int OFFSET = 1;
            argument = messageSplit[messageSplit.length - OFFSET];
            StringBuilder commandBuilder = new StringBuilder();
            int counter = 0;
            for (String i : messageSplit) {
                if (counter != 0) commandBuilder.append(" ");
                if (counter != messageSplit.length - OFFSET) {
                    commandBuilder.append(i);
                    counter++;
                }
            }
            command = commandBuilder.toString();
        } else {
            command = stringMessage;
        }
        command = command.toUpperCase();
        return new String[]{command, argument};
    }
}
