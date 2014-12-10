package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Client;
import com.nabusdev.padmedvbts2.model.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ForwardingProcess implements Runnable {
    private Stream stream;
    private static Logger logger = LoggerFactory.getLogger(ForwardingProcess.class);

    ForwardingProcess(Stream stream) {
        this.stream = stream;
    }

    public void run() {
        URLConnection connection = getConnection();
        if (connection == null) { logger.error("Timeout for connection to internal stream input"); return; }
        InputStream in = getInputStream(connection);
        if (in == null) { logger.error("Error during getting input stream of connection"); return; }
        startWriting(in);
    }

    private void startWriting(InputStream in) {
        while (true) {
            try {
                byte[] arr = new byte[4096];
                int readByte = in.read(arr);
                if (readByte != -1) {
                    List<Client> clientList = copyList(stream.getClients());
                    for (Client client : clientList) {
                        OutputStream out = client.getDataOutputStream();
                        try {
                            out.write(arr, 0, readByte);
                            client.resetWriteErrorCount();
                        } catch (IOException e) {
                            client.increaseWriteErrorCount();
                            final int MAX_ERROR_COUNT = 20000;
                            if (client.getWriteErrorCount() >= MAX_ERROR_COUNT) {
                                final String clientIp = client.getChannel().getInetAddress().toString().replace("/", "");
                                logger.info("Client " + clientIp + " dropped due to write errors.");
                                stream.removeClient(client);
                                client.getChannel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Client> copyList(List<Client> oldList) {
        List<Client> newList = new ArrayList<>();
        for (Client client : oldList) {
            newList.add(client);
        }
        return newList;
    }

    private URLConnection getConnection() {
        int tryCount = 0;
        final int MAX_TRY_COUNT = 60;

        while (tryCount < MAX_TRY_COUNT) {
            try {
                final String connectUrl = "http://" + stream.getIp() + ":" + stream.getPort() + stream.getPath();
                URL url = new URL(connectUrl);
                URLConnection connection = url.openConnection();
                return connection;

            } catch (IOException e) {
                try {
                    final int MILLIS_IN_1SEC = 1000;
                    Thread.sleep(MILLIS_IN_1SEC);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tryCount++;
        }
        return null;
    }

    private InputStream getInputStream(URLConnection connection) {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Stream getStream() {
        return stream;
    }
}
