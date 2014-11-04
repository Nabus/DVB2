package com.nabusdev.padmedvbts2;
import com.nabusdev.padmedvbts2.service.Database;
import com.nabusdev.padmedvbts2.service.DatabaseLoader;
import com.nabusdev.padmedvbts2.service.PropertiesLoader;

public class Main {

    public static void main(String[] args) {
        PropertiesLoader.load("server");
        Database.connect();
        DatabaseLoader.load();
    }
}
