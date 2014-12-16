package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Client;
import com.nabusdev.padmedvbts2.model.Forward;
import com.nabusdev.padmedvbts2.model.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpServer implements Runnable, ForwardProcess {
    private Forward forward;
    private ServerSocket network = null;
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    HttpServer(Forward forward) {
        this.forward = forward;
    }

    public void run() {
        try {
            runHandler();
        }
        catch (UnknownHostException e) {
            logger.error("Can't bind IP/Host address " + forward.getOutputStreamHost());
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (network != null) {
            try { network.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void runHandler() throws Exception {
        final int BACKLOG = 0;
        int port = forward.getOutputStreamPort();
        InetAddress host = InetAddress.getByName(forward.getOutputStreamHost());
        network = new ServerSocket(port, BACKLOG, host);
        try {
            while (!network.isClosed()) {
                Socket socket = network.accept();
                Client client = new Client(socket, forward);
                boolean isReadyToAccept = (forward.getChannel().getStream() != null);

                if (isReadyToAccept) {
                    if (!isClientLimitReached()) {
                        new Thread(new AcceptClient(client, forward)).start();
                    } else {
                        logger.info("Client "+ client.getIp() +" dropped. Clients limit exceeded");
                        client.drop();
                    }
                } else {
                    logger.debug("Client "+ client.getIp() +" dropped. Not ready to accept.");
                    client.drop();
                }

                forward.increaseClientConnectionAttempts();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isClientLimitReached() {
        int counter = 0;
        for (Client client : Client.getClientsByForward(forward)) counter++;
        if (counter >= forward.getOutputStreamClientLimit()) return true;
        return false;
    }

    class AcceptClient implements Runnable {
        private Client client;
        private Forward forward;

        AcceptClient(Client client, Forward forward) {
            this.client = client;
            this.forward = forward;
        }

        public void run() {
            logger.info("Client " + client.getIp() + " connected to forward ID: " +
                    forward.getId() + " channel ID: " + forward.getChannel().getId());
            writeResponse(client);
            Stream stream = forward.getChannel().getStream();
            stream.addClient(client);
        }

        private void writeResponse(Client client) {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: PadmeDVBTS2\r\n" +
                    "Content-Type: video/mpeg\r\n" +
                    "Connection: close\r\n\r\n";
            try {
                OutputStream outputStream = client.getDataOutputStream();
                outputStream.write(response.getBytes());
                outputStream.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
