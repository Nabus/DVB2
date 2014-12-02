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
        setTimer(channel);
        String pathToSave = channel.getThumbPath();
        SimpleDateFormat dateFormat = new SimpleDateFormat(channel.getThumbFilenamePattern());
        String fileName = dateFormat.format(new Date());
        String format = getValidFormat(channel.getThumbSaveFormat());
        int fileSize = saveThumbnail(channel, pathToSave, fileName, format);
        String query = "INSERT INTO " + TABLE_NAME + " (" + CHANNEL_ID + "," + PATH + "," + FILENAME + "," +
                SIZE + "," + FORMAT + "," + ACTIVE + "," + DATE_CREATED + ") VALUES (" + channel.getId() + ",'" +
                pathToSave + "','" + fileName + "'," + fileSize + ",'" + format + "');";
        db.execSql(query);
    }

    private int saveThumbnail(Channel channel, String pathToSave, String fileName, String format) {
        File thumbSource = new File(JAVA_EXEC_PATH + File.separator + "thumbSource.video");
        if (thumbSource.exists()) thumbSource.delete();
        Stream stream = channel.getStream();
        try {
            FileOutputStream fileStream = new FileOutputStream(thumbSource);
            URL url = new URL("http://" + stream.getIp() + ":" + stream.getPort() + stream.getPath());
            URLConnection connection = url.openConnection();
            long startRecord = System.currentTimeMillis();
            int MILLIS_FACTOR = 1000;
            while (System.currentTimeMillis() - startRecord <= MILLIS_FACTOR) {
                byte[] arr = new byte[4096];
                int readByte = connection.getInputStream().read(arr);
                if (readByte != -1) {
                    fileStream.write(arr, 0, readByte);
                }
            }
            fileStream.close();
            File thumbFile;
            int FRAME_NUMBER = 1;
            BufferedImage frame = FrameGrab.getFrame(thumbSource, FRAME_NUMBER);
            ImageIO.write(frame, format, thumbFile = new File(pathToSave + File.separator + fileName + "." + format));
            return (int) thumbFile.length();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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
