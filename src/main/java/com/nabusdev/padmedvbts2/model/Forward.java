package com.nabusdev.padmedvbts2.model;

public class Forward {
    private int id;
    private Channel channel;
    private ForwardStatus eventType;
    private String outputStreamProtocol;
    private String outputStreamHost;
    private int outputStreamPort;
    private String outputStreamUsername;
    private String outputStreamPassword;
    private String outputStreamUrlPath;
    private int outputStreamTimeout = 30000;
    private int outputStreamClientLimit = 1;

    public Forward(int id, Channel channel, String outputStreamProtocol, String outputStreamHost, int outputStreamPort) {
        this.id = id;
        this.channel = channel;
        this.outputStreamProtocol = outputStreamProtocol;
        this.outputStreamHost = outputStreamHost;
        this.outputStreamPort = outputStreamPort;
    }

    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ForwardStatus getEventType() {
        return eventType;
    }

    public void setEventType(ForwardStatus eventType) {
        this.eventType = eventType;
    }

    public String getOutputStreamProtocol() {
        return outputStreamProtocol;
    }

    public void setOutputStreamProtocol(String outputStreamProtocol) {
        this.outputStreamProtocol = outputStreamProtocol;
    }

    public String getOutputStreamHost() {
        return outputStreamHost;
    }

    public void setOutputStreamHost(String outputStreamHost) {
        this.outputStreamHost = outputStreamHost;
    }

    public int getOutputStreamPort() {
        return outputStreamPort;
    }

    public void setOutputStreamPort(int outputStreamPort) {
        this.outputStreamPort = outputStreamPort;
    }

    public String getOutputStreamUsername() {
        return outputStreamUsername;
    }

    public void setOutputStreamUsername(String outputStreamUsername) {
        this.outputStreamUsername = outputStreamUsername;
    }

    public String getOutputStreamPassword() {
        return outputStreamPassword;
    }

    public void setOutputStreamPassword(String outputStreamPassword) {
        this.outputStreamPassword = outputStreamPassword;
    }

    public String getOutputStreamUrlPath() {
        return outputStreamUrlPath;
    }

    public void setOutputStreamUrlPath(String outputStreamUrlPath) {
        this.outputStreamUrlPath = outputStreamUrlPath;
    }

    public int getOutputStreamTimeout() {
        return outputStreamTimeout;
    }

    public void setOutputStreamTimeout(int outputStreamTimeout) {
        this.outputStreamTimeout = outputStreamTimeout;
    }

    public int getOutputStreamClientLimit() {
        return outputStreamClientLimit;
    }

    public void setOutputStreamClientLimit(int outputStreamClientLimit) {
        this.outputStreamClientLimit = outputStreamClientLimit;
    }
}
