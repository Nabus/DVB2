package com.nabusdev.padmedvbts2.model;
import java.util.ArrayList;
import java.util.List;

public class Stream {
    private Channel channel;
    private String path;
    private int serviceId;
    private List<Client> clients = new ArrayList<>();
    private static List<Stream> streams = new ArrayList<>();

    public Stream() {
        streams.add(this);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public static List<Stream> getStreams() {
        return streams;
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public List<Client> getClients() {
        return clients;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getIp() {
        return channel.getAdapter().getServiceIp();
    }

    public int getPort() {
        return channel.getAdapter().getServicePort();
    }

    public String getPath() {
        final String URL_SLASH = "/";
        if (path == null) path = URL_SLASH;
        else if (!path.startsWith(URL_SLASH)) return URL_SLASH + path;
        return path;
    }

    public int getServiceId() {
        return serviceId;
    }
}
