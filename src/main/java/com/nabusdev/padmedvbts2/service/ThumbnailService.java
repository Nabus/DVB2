package com.nabusdev.padmedvbts2.service;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThumbnailService {
    private static Map<String, String> formats = init();
    private static Logger logger = LoggerFactory.getLogger(ThumbnailService.class);

    private static Map<String, String> init() {
        Map<String, String> formats = new HashMap<>();
        formats.put("JPEG", "jpg");
        formats.put("PNG", "png");
        return formats;
    }

    public void createThumb(String format, String path) {
        if (isInvalidFormat(format)) {
            logger.error("Specified format '"+ format +"' is not supported");
            return;
        }
        // TODO
        String line = "someprogram -makethumb -path " + path;
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        try {
            int exitValue = executor.execute(cmdLine);
        } catch (ExecuteException e) {
            logger.error("Failed to execute thumbnail generator");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isInvalidFormat(String format) {
        if (formats.containsKey(format)) return false;
        return true;
    }
}
