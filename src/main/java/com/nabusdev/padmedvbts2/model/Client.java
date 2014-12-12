package com.nabusdev.padmedvbts2.model;
import com.nabusdev.padmedvbts2.service.ForwardWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private int clientID;
    private Socket channel;
    private ForwardWriter writer;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static volatile int clientLastID = 0;
    private static Map<Integer, Client> clients = new HashMap<>();

    public Client(Socket channel) {
        this.channel = channel;
        this.dataInputStream = createDataInputStream();
        this.dataOutputStream = createDataOutputStream();
        this.clientID = ++clientLastID;
        this.addToList();
    }

    private void addToList() {
        clients.put(getId(), this);
    }

    private DataInputStream createDataInputStream(){
        try{ return new DataInputStream(getChannel().getInputStream()); }
        catch (IOException e) {e.printStackTrace();}
        return null;
    }

    private DataOutputStream createDataOutputStream(){
        try{ return new DataOutputStream(getChannel().getOutputStream()); }
        catch (IOException e) {e.printStackTrace();}
        return null;
    }

    public DataInputStream getDataInputStream(){
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream(){
        return dataOutputStream;
    }

    public int getId(){
        return clientID;
    }

    public Socket getChannel() {
        return channel;
    }

    public ForwardWriter getWriter() {
        return writer;
    }

    public void setWriter(ForwardWriter writer) {
        this.writer = writer;
    }
}
