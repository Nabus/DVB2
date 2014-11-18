package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Forward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamService {
    private static final StreamService INSTANCE = new StreamService();
    private static Logger logger = LoggerFactory.getLogger(StreamService.class);

    public static void init() {
        StreamInitialization.init();
        for (Channel channel : Channel.getChannelList()) {
            for (Forward forward : channel.getForwards()) {
                String protocol = forward.getOutputStreamProtocol();
                if (protocol.equals("HTTP")) INSTANCE.startHttpServer(forward);
                else logger.warn("Forward skipped, only HTTP supported");
            }
        }
    }

    private void startHttpServer(Forward forward) {
        new Thread(new HttpServer(forward)).start();
    }
}
