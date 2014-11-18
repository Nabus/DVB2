package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Channel;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ThumbnailService {
    private static final Map<String, String> formats = getFormats();
    private static final ThumbnailService INSTANCE = new ThumbnailService();
    private static Logger logger = LoggerFactory.getLogger(ThumbnailService.class);

    public static void init() {
        for (Channel channel : Channel.getChannelList()) {
            INSTANCE.setTimer(channel);
        }
    }

    private void setTimer(final Channel channel) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                createThumb(channel);
            }
        };
        int captureInterval = channel.getThumbSavePeriod();
        timer.schedule(timerTask, captureInterval);
    }

    private void createThumb(Channel channel) {
        setTimer(channel);
        // TODO
    }

    private static Map<String, String> getFormats() {
        Map<String, String> formats = new HashMap<>();
        formats.put("JPEG", "jpg");
        formats.put("PNG", "png");
        return formats;
    }

    public boolean isInvalidFormat(String format) {
        if (formats.containsKey(format)) return false;
        return true;
    }
}
