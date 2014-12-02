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
        startWriting(in);
    }

    private URLConnection getConnection() {
        int tryCount = 0;
        final int MAX_TRY_COUNT = 60;
        final int MILLIS_IN_1SEC = 1000;

        while (tryCount < MAX_TRY_COUNT) {
            try {
                URL url = new URL("http://" + stream.getIp() + ":" + stream.getPort() + stream.getPath());
                URLConnection connection = url.openConnection();
                return connection;
            } catch (IOException e) {
                /* Ignoring, Sleeping for 1 sec */
                try {
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
            InputStream in = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startWriting(InputStream in) {
        while (true) {
            try {
                byte[] arr = new byte[4096];
                int readByte = in.read(arr);
                if (readByte != -1) {
                    List<Client> clientList = stream.getClients();
                    for (Client client : clientList) {
                        OutputStream out = client.getDataOutputStream();
                        try {
                            out.write(arr, 0, readByte);
                        } catch (IOException e) {
                            /* Ignoring */
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Stream getStream() {
        return stream;
    }
}
