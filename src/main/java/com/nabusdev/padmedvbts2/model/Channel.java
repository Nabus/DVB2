package com.nabusdev.padmedvbts2.model;

public class Channel {
    private String name;
    private String ident;
    private Adapter adapter;
    private int pnrId;
    private String thumbPath;
    private String thumbFilePattern;
    private int thumbGenerateInterval = 15000;
    private String thumbImageFormat = "JPEG";

    Channel(String name, String ident, Adapter adapter, int pnrId) {
        this.name = name;
        this.ident = ident;
        this.adapter = adapter;
        this.pnrId = pnrId;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setThumbFilePattern(String thumbFilePattern) {
        this.thumbFilePattern = thumbFilePattern;
    }

    public void setThumbGenerateInterval(int thumbGenerateInterval) {
        this.thumbGenerateInterval = thumbGenerateInterval;
    }

    public void setThumbImageFormat(String thumbImageFormat) {
        this.thumbImageFormat = thumbImageFormat;
    }

    public void startUse() {

    }

    public void stopUse() {

    }

    public void errorOccurred(String errorMessage) {

    }
}
