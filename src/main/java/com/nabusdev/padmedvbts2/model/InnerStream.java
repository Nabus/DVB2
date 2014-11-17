package com.nabusdev.padmedvbts2.model;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class InnerStream {
    private Channel channel;
    private String ip;
    private int port;
    private List<Client> listeners = new ArrayList<>();
    private static List<InnerStream> innerStreams = new ArrayList<>();
    private boolean isForwarding = false;

    public InnerStream(Channel channel, String ip, int port) {
        this.channel = channel;
        this.ip = ip;
        this.port = port;
        innerStreams.add(this);
    }

    public void addClient(Client client) {
        listeners.add(client);
    }

    public List<Client> getClientList() {
        return listeners;
    }

    public static List<InnerStream> getInnerStreams() {
        return innerStreams;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isForwarding() {
        return isForwarding;
    }

    public void startForward() {

    }
}
