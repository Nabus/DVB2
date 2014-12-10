package com.nabusdev.padmedvbts2.model;

public class Programme {
    private int channelId;
    private int channelPnrId;
    private long start;
    private long stop;
    private String title;
    private String titleLang;
    private String subTitle;
    private String subTitleLang;
    private String desc;
    private String descLang;
    private String language;
    private String videoAspect;
    private String audioStereo;
    private String ratingSystem;
    private String ratingValue;
    private String subtitlesType;
    private String subtitlesLanguage;

    public Programme() {

    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getChannelPnrId() {
        return channelPnrId;
    }

    public void setChannelPnrId(int channelPnrId) {
        this.channelPnrId = channelPnrId;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleLang() {
        return titleLang;
    }

    public void setTitleLang(String titleLang) {
        this.titleLang = titleLang;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubTitleLang() {
        return subTitleLang;
    }

    public void setSubTitleLang(String subTitleLang) {
        this.subTitleLang = subTitleLang;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescLang() {
        return descLang;
    }

    public void setDescLang(String descLang) {
        this.descLang = descLang;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVideoAspect() {
        return videoAspect;
    }

    public void setVideoAspect(String videoAspect) {
        this.videoAspect = videoAspect;
    }

    public String getAudioStereo() {
        return audioStereo;
    }

    public void setAudioStereo(String audioStereo) {
        this.audioStereo = audioStereo;
    }

    public String getRatingSystem() {
        return ratingSystem;
    }

    public void setRatingSystem(String ratingSystem) {
        this.ratingSystem = ratingSystem;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getSubtitlesType() {
        return subtitlesType;
    }

    public void setSubtitlesType(String subtitlesType) {
        this.subtitlesType = subtitlesType;
    }

    public String getSubtitlesLanguage() {
        return subtitlesLanguage;
    }

    public void setSubtitlesLanguage(String subtitlesLanguage) {
        this.subtitlesLanguage = subtitlesLanguage;
    }
}
