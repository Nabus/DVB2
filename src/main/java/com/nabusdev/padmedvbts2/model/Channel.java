package com.nabusdev.padmedvbts2.model;

public class Channel {
    private String name;
    private String ident;
    private Adapter adapter;
    private int pnrId;
    private String thumbPath;
    private String thumbFilenamePattern;
    private int thumbSavePeriod = 15000;
    private String thumbSaveFormat = "JPEG";

    public Channel(String name, String ident, Adapter adapter, int pnrId) {
        this.name = name;
        this.ident = ident;
        this.adapter = adapter;
        this.pnrId = pnrId;
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
