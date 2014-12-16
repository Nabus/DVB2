package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;

public class ForwardWriter implements Runnable {
    private boolean isWriting = false;
    private boolean gotNewPackets = false;
    private byte[] bufferArray;
    private int readByte;
    private Client client;
    private ForwardReader reader;
    private long lastActivity;
    private boolean continueWriting;
    private static Logger logger = LoggerFactory.getLogger(ForwardWriter.class);

    ForwardWriter(Client client, ForwardReader reader) {
        this.client = client;
        this.reader = reader;
        continueWriting = true;
        client.setWriter(this);
    }

    public void run() {
        while (continueWriting) {
            if (gotNewPackets) {
                try {
                    writeData();
                    resetLastActivity();
                    gotNewPackets = false;
                } catch (Exception e) {
                    if (System.currentTimeMillis() - getLastActivity() >= getOutputStreamTimeout()) {
                        logger.info("Client " + client.getIp() + " dropped. Inactivity timeout exceeded.");
                        eliminateClient();
                    }
                }
            }
        }
    }

    private int getOutputStreamTimeout() {
        return client.getForward().getOutputStreamTimeout();
    }

    public void updateBuffer(byte[] bufferArray, int readByte) {
        if (!isWriting) {
            this.bufferArray = bufferArray;
            this.readByte = readByte;
            gotNewPackets = true;
        }
    }

    private void writeData() throws IOException {
        if (readByte != -1) {
            isWriting = true;
            OutputStream out = client.getDataOutputStream();
            out.write(bufferArray, 0, readByte);
            isWriting = false;
        }
    }

    private void eliminateClient() {
        continueWriting = false;
        reader.getStream().removeClient(client);
        try { client.getChannel().close(); }
        catch (Exception e) { /* Ignoring Exception */ }
    }

    private long getLastActivity() {
        return lastActivity;
    }

    private void resetLastActivity() {
        lastActivity = System.currentTimeMillis();
    }
}