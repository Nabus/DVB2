package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader extends ConfigLoader {
    private static final String PREFIX = JAVA_EXEC_PATH + File.separator + "resources" + File.separator;
    private static final String SUFFIX = ".properties";

    public static void load(String configPath) {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(PREFIX + configPath + SUFFIX)){
            properties.load(inputStream);
            for (Object objectKey : properties.keySet()) {
                String key = (String) objectKey;
                String value = properties.getProperty(key);
                setVariable(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
