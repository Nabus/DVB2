package com.nabusdev.padmedvbts2;
import com.nabusdev.padmedvbts2.service.ForwardingService;
import com.nabusdev.padmedvbts2.service.StreamingService;
import com.nabusdev.padmedvbts2.service.config.ChannelLoader;
import com.nabusdev.padmedvbts2.service.config.DatabaseLoader;
import com.nabusdev.padmedvbts2.service.config.ForwardLoader;
import com.nabusdev.padmedvbts2.service.config.PropertiesLoader;
import com.nabusdev.padmedvbts2.service.telnet.TelnetServer;

public class Main {

    public static void main(String[] args) {
        PropertiesLoader.load("server");
        DatabaseLoader.load();
        TelnetServer.init();
        ChannelLoader.load();
        ForwardLoader.load();
        StreamingService.init();

    }
}
