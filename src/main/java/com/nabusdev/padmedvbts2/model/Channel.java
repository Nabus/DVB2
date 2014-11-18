package com.nabusdev.padmedvbts2.model;
import com.nabusdev.padmedvbts2.service.ForwardingProcess;

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
    private Stream stream;
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

    public static List<Channel> getChannelList() {
        List<Channel> channelList = new ArrayList<>();
        for (Channel channel : channels.values()) {
            channelList.add(channel);
        }
        return channelList;
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

    public String getThumbSaveFormat() {
        return thumbSaveFormat;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public String getThumbFilenamePattern() {
        return thumbFilenamePattern;
    }

    public int getThumbSavePeriod() {
        return thumbSavePeriod;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
