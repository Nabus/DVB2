package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import static com.nabusdev.padmedvbts2.util.Constants.Table.ChannelThumbs.*;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Stream;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import org.jcodec.api.FrameGrab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThumbnailService {
    private static Database db = DatabaseProvider.getChannelsDB();
    private static final Map<String, String> formats = getAllowedFormats();
    private static final ThumbnailService INSTANCE = new ThumbnailService();
    private static Logger logger = LoggerFactory.getLogger(ThumbnailService.class);

    public static void scheduleAll() {
        for (Channel channel : Channel.getChannelList()) {
            schedule(channel);
        }
    }

    public static void schedule(Channel channel) {
        INSTANCE.setTimer(channel);
    }

    private void setTimer(final Channel channel) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                createThumb(channel);
            }
        };
        int captureInterval = channel.getThumbSavePeriod();
        timer.scheduleAtFixedRate(timerTask, captureInterval, captureInterval);
    }

    private void createThumb(Channel channel) {
        String pathToSave = channel.getThumbPath();
        SimpleDateFormat dateFormat = new SimpleDateFormat(channel.getThumbFilenamePattern());
        String fileName = dateFormat.format(new Date());
        String format = getValidFormat(channel.getThumbSaveFormat());
        int fileSize = saveThumbnail(channel, pathToSave, fileName, format);
        if (fileSize > 0) {
            String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (%d, '%s', '%s', %d, '%s', 1, now())",
                    TABLE_NAME, CHANNEL_ID, PATH, FILENAME, SIZE, FORMAT, ACTIVE, DATE_CREATED,
                    channel.getId(), pathToSave, fileName, fileSize, format);
            db.execSql(query);
        }
    }

    private int saveThumbnail(Channel channel, String pathToSave, String fileName, String format) {
        final int MILLIS_FACTOR = 1000;
        final int RECORD_SEC_COUNT = 5;
        final String THUMBNAIL_FILE_PATH = JAVA_EXEC_PATH + File.separator + "thumbSource.video";
        File thumbSource = StreamRecorder.record(THUMBNAIL_FILE_PATH, channel, RECORD_SEC_COUNT * MILLIS_FACTOR);
        if (thumbSource == null) return 0;
        try {
            int FRAME_NUMBER = 10;
            BufferedImage frame = FrameGrab.getFrame(thumbSource, FRAME_NUMBER);
            File thumbFile = new File(pathToSave + File.separator + fileName + "." + format);
            ImageIO.write(frame, format, thumbFile);
            if (thumbSource.exists()) thumbSource.delete();
            return (int) thumbFile.length();

        } catch (Exception e) {
            // TODO e.printStackTrace();
            return 0;

        } finally {
            if (thumbSource.exists()) {
                thumbSource.delete();
            }
        }
    }

    private static String getValidFormat(String format) {
        if (formats.containsKey(format)) {
            return formats.get(format);
        } else {
            for (String validFormat : formats.values()) {
                logger.error("Defined thumbnail format '" + format + "' is not supported. Setting default '" + validFormat + "'");
                return validFormat;
            }
        }
        logger.error("Can't find any supported thumbnail format");
        return null;
    }

    private static Map<String, String> getAllowedFormats() {
        if (formats != null) return formats;
        Map<String, String> formats = new HashMap<>();
        formats.put("JPEG", "jpg");
        formats.put("PNG", "png");
        return formats;
    }
}
