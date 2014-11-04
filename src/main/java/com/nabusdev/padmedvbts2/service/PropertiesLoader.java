package com.nabusdev.padmedvbts2.service;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader extends ConfigLoader {
    private static final String SUFFIX = ".properties";

    public static void load(String configPath) {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(configPath + SUFFIX)){
            properties.load(inputStream);
            for (Object objectKey : properties.keySet()) {
                String key = (String) objectKey;
                String value = properties.getProperty(key);
                createVariable(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
