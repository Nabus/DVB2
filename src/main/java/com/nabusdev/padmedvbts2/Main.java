package com.nabusdev.padmedvbts2;
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
        StreamService.init();
        TelnetServer.init();
    }
}
