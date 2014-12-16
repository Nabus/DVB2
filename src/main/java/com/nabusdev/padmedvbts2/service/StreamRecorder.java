package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class StreamRecorder {
    private static Logger logger = LoggerFactory.getLogger(StreamRecorder.class);

    static public File record(String path, Channel channel, int millis) {
        return record(path, channel.getStream(), millis);
    }

    static public File record(String path, Stream stream, int millis) {
        try {
            File file = new File(path);
            if (file.exists()) file.delete();
            FileOutputStream fileStream = new FileOutputStream(file);
            URL url = new URL("http://" + stream.getIp() + ":" + stream.getPort() + stream.getPath());
            URLConnection connection = url.openConnection();
            long startRecord = System.currentTimeMillis();
            while (System.currentTimeMillis() - startRecord <= millis) {
                byte[] arr = new byte[4096];
                int readByte = connection.getInputStream().read(arr);
                if (readByte != -1) {
                    fileStream.write(arr, 0, readByte);
                }
            }
            fileStream.flush();
            fileStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("Unable to record stream. Returning null");
        return null;
    }
}
