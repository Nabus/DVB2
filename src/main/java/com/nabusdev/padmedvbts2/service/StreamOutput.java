package com.nabusdev.padmedvbts2.service;

import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Forward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamOutput {
    private static final StreamOutput INSTANCE = new StreamOutput();
    private static Logger logger = LoggerFactory.getLogger(StreamOutput.class);

    public static void start() {
        for (Channel channel : Channel.getChannelList()) {
            for (Forward forward : channel.getForwards()) {
                String protocol = forward.getOutputStreamProtocol();
                if (protocol.equals("HTTP")) INSTANCE.startHttpServer(forward);
                else throw new UnsupportedOperationException("Not supported yet.");
            }
        }
    }

    public static void stop() {
        // TODO
    }

    private void startHttpServer(Forward forward) {
        new Thread(new HttpServer(forward)).start();
    }
}
