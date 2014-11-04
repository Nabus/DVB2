package com.nabusdev.padmedvbts2.service.telnet;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import static com.nabusdev.padmedvbts2.util.Constants.Telnet.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class TelnetServer {

    public static void init() {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(
                Charset.defaultCharset(), System.lineSeparator(), System.lineSeparator())));
        acceptor.setHandler(new TelnetHandler());
        try { acceptor.bind(new InetSocketAddress(PORT)); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
