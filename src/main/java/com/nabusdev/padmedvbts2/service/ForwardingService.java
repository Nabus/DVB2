package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ForwardingService {
    private static Logger logger = LoggerFactory.getLogger(ForwardingService.class);
    private static final ForwardingService INSTANCE = new ForwardingService();
    private static final int TEMP_PORT = 27000;
    private static final int TIMEOUT_SEC = 25;

    private ForwardingService() {

    }

    public static ForwardingService getInstance() {
        return INSTANCE;
    }

    public static void init() {
        getInstance().initHandler();
    }

    public void initHandler() {
        try (ServerSocket network = new ServerSocket(TEMP_PORT)){
            logger.info("Accepting connections at port " + TEMP_PORT);
            while (true){
                new Thread(new Channel(network.accept())).start();
            }
        }
        catch (BindException e){
            logger.error("Failed to bind port " + TEMP_PORT);
            logger.error(e.getMessage());
            System.exit(1);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    class Channel implements Runnable {
        private Client client;
        private Socket channel;
        private int timeoutCount = 0;

        public Channel(Socket channel){
            this.client = new Client(channel);
            this.channel = channel;
        }

        public void run(){
            new Thread(new ChannelListen(client, channel)).start();
            try{
                DataOutputStream disOut = client.getDataOutputStream();
                while (!channel.isClosed()){
                    try {Thread.sleep(1000);}
                    catch (Exception e) {e.printStackTrace();}
                    try {
                        disOut.writeByte(0);
                        disOut.flush();
                        timeoutCount = 0;
                    }
                    catch (Exception e){
                        timeoutCount++;
                        if (timeoutCount == TIMEOUT_SEC){
                            dropChannel(channel);
                        }
                    }
                }
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    class ChannelListen implements Runnable{
        private Socket channel;
        private Client client;

        public ChannelListen(Client client, Socket channel){
            this.channel = channel;
            this.client = client;
        }

        public void run(){
            String inStreamData;
            DataInputStream inStream = client.getDataInputStream();
            while (!channel.isClosed()){
                try {
                    inStreamData = inStream.readUTF();
                    // todo
                    //PacketParser.parseNetworkPacket(client, inStreamData);
                }
                catch (Exception e) {/* Ignoring "Connection Reset" Exception */}
            }
        }
    }

    public static void sendPacket(Client client, String packet){
        try{
            DataOutputStream outputStream = client.getDataOutputStream();
            outputStream.writeUTF(packet);
            outputStream.flush();
        }
        catch (SocketException e) {/* Ignoring "Socket Write Error" Exception */}
        catch (IOException e) {e.printStackTrace();}
    }

    public static void dropChannel(Socket channel){
        try {channel.close();}
        catch (IOException e) {e.printStackTrace();}
    }
}
