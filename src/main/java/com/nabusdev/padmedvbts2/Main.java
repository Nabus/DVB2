package com.nabusdev.padmedvbts2;
import com.nabusdev.padmedvbts2.service.epg.EpgCollector;
import com.nabusdev.padmedvbts2.service.telnet.TelnetServer;
import com.nabusdev.padmedvbts2.service.config.*;
import com.nabusdev.padmedvbts2.service.*;

public class Main {

    public static void main(String[] args) {
        PropertiesLoader.load("server");
        DatabaseLoader.load();
        AdapterLoader.load();
        ChannelLoader.load();
        ForwardLoader.load();
        StreamInput.start();
        StreamOutput.start();
        TelnetServer.start();
        EpgCollector.scheduleAll();
        ThumbnailService.scheduleAll();
        ThroughputService.scheduleAll();
    }
}
