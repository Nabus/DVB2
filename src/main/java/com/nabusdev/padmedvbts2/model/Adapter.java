package com.nabusdev.padmedvbts2.model;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import static com.nabusdev.padmedvbts2.util.Constants.Table.Adapters.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter {
    private int id;
    private String path;
    private String ident;
    private String adapterType;
    private int frequency;
    private int bandwidth;
    private String transmissionMode;
    private String guardInterval;
    private String hierarchy;
    private String serviceIp;
    private int servicePort;
    private List<Channel> channels = new ArrayList<>();
    private static Map<Integer, Adapter> adapters = new HashMap<>();
    private static Database db = DatabaseProvider.getChannelsDB();

    public Adapter(int id, String ident, String path) {
       this.id = id;
       this.path = path;
       this.ident = ident;
       adapters.put(id, this);
    }

    public static Adapter getById(int id) {
        return adapters.get(id);
    }

    public static List<Adapter> getAdapterList() {
        List<Adapter> adapterList = new ArrayList<>();
        for (Adapter adapter : adapters.values()) {
            adapterList.add(adapter);
        }
        return adapterList;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void setAdapterType(String adapterType) {
        this.adapterType = adapterType;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public void setTransmissionMode(String transmissionMode) {
        this.transmissionMode = transmissionMode;
    }

    public void setGuardInterval(String guardInterval) {
        this.guardInterval = guardInterval;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public int getId(){
        return id;
    }

    public String getAdapterType() {
        return adapterType;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public int getFrequency() {
        return frequency;
    }

    public void notifyStartUsing() {
        db.execSql("UPDATE " + TABLE_NAME + " SET " + DATE_START + " = UNIX_TIMESTAMP();");
    }

    public void notifyStopUsing() {
        db.execSql("UPDATE " + TABLE_NAME + " SET " + DATE_STOP + " = UNIX_TIMESTAMP();");
    }

    public void notifyFailOccurred(String failedMessage) {
        db.execSql("UPDATE " + TABLE_NAME + " SET " + DATE_FAILED + " = UNIX_TIMESTAMP(), " + FAILED_MESSAGE + " = '" + failedMessage + "';");
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }
}
