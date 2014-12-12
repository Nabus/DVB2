package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Client;
import com.nabusdev.padmedvbts2.model.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ForwardReader implements Runnable {
    private Stream stream;
    private static Logger logger = LoggerFactory.getLogger(ForwardReader.class);

    ForwardReader(Stream stream) {
        this.stream = stream;
    }

    public void run() {
        URLConnection connection = getConnection();
        if (connection != null) {
            InputStream in = getInputStream(connection);
            if (in != null) {
                startReading(in);
            } else {
                logger.error("Error during getting input stream of connection");
            }
        } else {
            logger.error("Timeout for connection to internal stream input");
        }
    }

    private void startReading(InputStream in) {
        while (true) {
            try {
                byte[] bufferArray = new byte[4096];
                int readByte = in.read(bufferArray);
                List<Client> clientList = copyList(stream.getClients());
                for (Client client : clientList) {
                    boolean isWriterCreated = (client.getWriter() != null);
                    if (!isWriterCreated) new Thread(new ForwardWriter(client, this)).start();
                    ForwardWriter writer = client.getWriter();
                    if (writer != null) writer.updateBuffer(bufferArray, readByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private List<Client> copyList(List<Client> oldList) {
        List<Client> newList = new ArrayList<>();
        for (Client client : oldList) {
            newList.add(client);
        }
        return newList;
    }

    public Stream getStream() {
        return stream;
    }
}
