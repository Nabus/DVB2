package com.nabusdev.padmedvbts2.model;

import java.util.List;
import java.util.Map;

public class Programme {
    private int id;
    private Channel channel;
    private int channelPnr;
    private long start;
    private long stop;
    private List<Map.Entry<String, String>> titles;
    private List<Map.Entry<String, String>> subtitles;
    private List<Map.Entry<String, String>> descriptions;
    private List<Map.Entry<String, String>> credits;
    private List<Map.Entry<String, String>> categories;
    private String audioStereo;
    private String videoAspect;
    private String videoQuality;
    private String subtitlesType;
    private String subtitlesLanguage;
    private String ratingSystem;
    private String ratingValue;
    private String language;
    private String origLanguage;
    private int length;
    private String icon;
    private String url;
    private String country;
    private int episodeNum;
    private String starRating;
    private long previouslyShown;
    private boolean isPremiere;
    private boolean isLastChance;
    private boolean isNew;

    public Programme() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getChannelPnr() {
        return channelPnr;
    }

    public void setChannelPnr(int channelPnr) {
        this.channelPnr = channelPnr;
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

    public List<Map.Entry<String, String>> getTitles() {
        return titles;
    }

    public void setTitles(List<Map.Entry<String, String>> titles) {
        this.titles = titles;
    }

    public List<Map.Entry<String, String>> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Map.Entry<String, String>> subtitles) {
        this.subtitles = subtitles;
    }

    public List<Map.Entry<String, String>> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<Map.Entry<String, String>> descriptions) {
        this.descriptions = descriptions;
    }

    public List<Map.Entry<String, String>> getCredits() {
        return credits;
    }

    public void setCredits(List<Map.Entry<String, String>> credits) {
        this.credits = credits;
    }

    public List<Map.Entry<String, String>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map.Entry<String, String>> categories) {
        this.categories = categories;
    }

    public String getAudioStereo() {
        return audioStereo;
    }

    public void setAudioStereo(String audioStereo) {
        this.audioStereo = audioStereo;
    }

    public String getVideoAspect() {
        return videoAspect;
    }

    public void setVideoAspect(String videoAspect) {
        this.videoAspect = videoAspect;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOrigLanguage() {
        return origLanguage;
    }

    public void setOrigLanguage(String origLanguage) {
        this.origLanguage = origLanguage;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(int episodeNum) {
        this.episodeNum = episodeNum;
    }

    public String getStarRating() {
        return starRating;
    }

    public void setStarRating(String starRating) {
        this.starRating = starRating;
    }

    public long getPreviouslyShown() {
        return previouslyShown;
    }

    public void setPreviouslyShown(long previouslyShown) {
        this.previouslyShown = previouslyShown;
    }

    public boolean isPremiere() {
        return isPremiere;
    }

    public void setPremiere(boolean isPremiere) {
        this.isPremiere = isPremiere;
    }

    public boolean isLastChance() {
        return isLastChance;
    }

    public void setLastChance(boolean isLastChance) {
        this.isLastChance = isLastChance;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
