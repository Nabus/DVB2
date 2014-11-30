package com.nabusdev.padmedvbts2.service.telnet;
import static com.nabusdev.padmedvbts2.util.Constants.Table.ServerSetup.*;
import com.nabusdev.padmedvbts2.util.Variable;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;

public class TelnetServer {

    public static void start() {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(
                Charset.defaultCharset(), System.lineSeparator(), System.lineSeparator())));
        acceptor.setHandler(new TelnetHandler());
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface interface_ : Collections.list(interfaces)) {
                String interfaceName = interface_.getName();
                if (interfaceName.equals((Variable.get(COMMAND_MANAGER_INTERFACE)))) {
                    Enumeration<InetAddress> addresses = interface_.getInetAddresses();
                    for (InetAddress address : Collections.list(addresses)) {
                        String hostName = address.getHostName();
                        if (hostName.equals(Variable.get(COMMAND_MANAGER_HOST))) {
                            int port = Integer.parseInt(Variable.get(COMMAND_MANAGER_PORT));
                            acceptor.bind(new InetSocketAddress(address, port));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
