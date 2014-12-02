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
                Client client = new Client(socket);
                Stream stream = forward.getChannel().getStream();
                writeResponse(client);
                stream.addClient(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
