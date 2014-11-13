package com.nabusdev.padmedvbts2.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private int clientID;
    private Socket channel;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static volatile int clientLastID = 0;
    private Map<Integer, Client> clients = new HashMap<>();

    public Client(Socket sChannel) {
        this.channel = sChannel;
        this.dataInputStream = createDataInputStream();
        this.dataOutputStream = createDataOutputStream();
        clientID = ++clientLastID;
        this.addToList();
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

    private void addToList() {
        clients.put(getId(), this);
    }

    public int getId(){
        return clientID;
    }

    public Socket getChannel() {
        return channel;
    }
}
