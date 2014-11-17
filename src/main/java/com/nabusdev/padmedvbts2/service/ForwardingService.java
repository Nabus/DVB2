package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Client;
import com.nabusdev.padmedvbts2.model.Forward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ForwardingService {
    private static final String HARDCODED_PORT = "27015";
    private static final String HARDCODED_IP = "192.168.1.7";
    private static final ForwardingService INSTANCE = new ForwardingService();
    private static Logger logger = LoggerFactory.getLogger(ForwardingService.class);

    public static void init() {
        for (Channel channel : Channel.getChannelList()) {
            for (Forward forward : channel.getForwards()) {
                String protocol = forward.getOutputStreamProtocol();
                if (protocol.equals("HTTP")) {
                    INSTANCE.startHttpServer(channel, forward);
                } else {
                    logger.warn("Forward skipped, only HTTP supported");
                }
            }
        }
    }

    private void startHttpServer(Channel channel, Forward forward) {
        new Thread(new HttpServer(forward)).start();
    }

    class HttpServer implements Runnable {
        private Forward forward;

        HttpServer(Forward forward) {
            this.forward = forward;
        }

        public void run() {
            int port = forward.getOutputStreamPort();
            InetAddress host;
            try { host = InetAddress.getByName(forward.getOutputStreamHost()); }
            catch (UnknownHostException e) {
                logger.error("Can't bind IP/Host address " + forward.getOutputStreamHost());
                e.printStackTrace();
                return;
            }

            final int BACKLOG = 0;
            try (ServerSocket network = new ServerSocket(port, BACKLOG, host)) {
                while (true) {
                    Socket socket = network.accept();
                    Client client = new Client(socket);
                    Channel channel = forward.getChannel();
                    ForwardingHandler.connect(client, channel);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
