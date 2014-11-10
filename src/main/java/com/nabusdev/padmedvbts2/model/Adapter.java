package com.nabusdev.padmedvbts2.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter {
    private static Map<Integer, Adapter> adapters = new HashMap<>();
    private int id;
    private String path;
    private String ident;
    private String adapterType;
    private int frequency;
    private int bandwidth;
    private String transmissionMode;
    private String guardInterval;
    private String hierarchy;

    public Adapter(int id, String ident, String path) {
       this.id = id;
       this.path = path;
       this.ident = ident;
       adapters.put(id, this);
    }

    public static Adapter getById(int id) {
        return adapters.get(id);
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
}
