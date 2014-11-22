package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.Table.ChannelThumbs.*;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThumbnailService {
    private static Database db = DatabaseProvider.getChannelsDB();
    private static final Map<String, String> formats = getAllowedFormats();
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
        String pathToSave = channel.getThumbPath();
        SimpleDateFormat dateFormat = new SimpleDateFormat(channel.getThumbFilenamePattern());
        String fileName = dateFormat.format(new Date());
        String format = getValidFormat(channel.getThumbSaveFormat());

        // TODO method int saveFile(pathToSave, fileName, format)
        int fileSize = 0;

        String query = "INSERT INTO " + TABLE_NAME + " (" + CHANNEL_ID + "," + PATH + "," + FILENAME + "," +
                SIZE + "," + FORMAT + "," + ACTIVE + "," + DATE_CREATED + ") VALUES (" + channel.getId() + ",'" +
                pathToSave + "','" + fileName + "'," + fileSize + ",'" + format + "');";
        db.execSql(query);
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
