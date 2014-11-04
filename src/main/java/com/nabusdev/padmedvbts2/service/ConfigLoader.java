package com.nabusdev.padmedvbts2.service;

public abstract class ConfigLoader {

    static void createVariable(String key, String value) {
        Variable.add(key, value);
    }
}
