package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;

public class ForwardWriter implements Runnable {
    private volatile boolean isWriting = false;
    private volatile boolean gotNewPackets = false;
    private byte[] bufferArray;
    private int readByte;
    private Client client;
    private ForwardReader reader;
    private int writeErrorCount;
    private boolean continueWriting;
    final int MAX_ERROR_COUNT = 20000;
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
                    gotNewPackets = false;
                    resetWriteErrorCount();
                } catch (Exception e) {
                    increaseWriteErrorCount();
                    if (getWriteErrorCount() >= MAX_ERROR_COUNT) {
                        final String clientIp = client.getChannel().getInetAddress().toString().replace("/", "");
                        logger.info("Client " + clientIp + " dropped due to write errors.");
                        eliminateClient();
                    }
                }
            }
        }
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

    private int getWriteErrorCount() {
        return writeErrorCount;
    }

    private void increaseWriteErrorCount() {
        writeErrorCount++;
    }

    private void resetWriteErrorCount() {
        writeErrorCount = 0;
    }
}