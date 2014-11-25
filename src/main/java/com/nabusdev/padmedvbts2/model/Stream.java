package com.nabusdev.padmedvbts2.model;
import java.util.ArrayList;
import java.util.List;

public class Stream {
    private Channel channel;
    private String ip;
    private int port;
    private List<Client> listeners = new ArrayList<>();
    private static List<Stream> streams = new ArrayList<>();

    public Stream(Channel channel, String ip, int port) {
        this.channel = channel;
        this.ip = ip;
        this.port = port;
        streams.add(this);
    }

    public void addClient(Client client) {
        listeners.add(client);
    }

    public List<Client> getClientList() {
        return listeners;
    }

    public static List<Stream> getStreams() {
        return streams;
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
}
