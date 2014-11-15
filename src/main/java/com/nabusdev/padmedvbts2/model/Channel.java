package com.nabusdev.padmedvbts2.model;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Channel {
    private int id;
    private String name;
    private String ident;
    private Adapter adapter;
    private int pnrId;
    private String thumbPath;
    private String thumbFilenamePattern;
    private int thumbSavePeriod = 15000;
    private String thumbSaveFormat = "JPEG";
    private InputStream inputStream = null;
    private List<Forward> forwards = new ArrayList<>();
    private static Map<Integer, Channel> channels = new HashMap<>();

    public Channel(int id, String name, String ident, Adapter adapter, int pnrId) {
        this.id = id;
        this.name = name;
        this.ident = ident;
        this.adapter = adapter;
        this.pnrId = pnrId;
        channels.put(id, this);
    }

    public static Channel getById(int id) {
        return channels.get(id);
    }

    public void addForward(Forward forward) {
        forwards.add(forward);
    }

    public List<Forward> getForwards() {
        return forwards;
    }

    public String getName() {
        return name;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setThumbFilenamePattern(String thumbFilenamePattern) {
        this.thumbFilenamePattern = thumbFilenamePattern;
    }

    public void setThumbSavePeriod(int thumbSavePeriod) {
        this.thumbSavePeriod = thumbSavePeriod;
    }

    public void setThumbSaveFormat(String thumbSaveFormat) {
        this.thumbSaveFormat = thumbSaveFormat;
    }
}
