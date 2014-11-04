package com.nabusdev.padmedvbts2.service.config;

import com.nabusdev.padmedvbts2.util.Variable;

public abstract class ConfigLoader {

    static void createVariable(String key, String value) {
        Variable.add(key, value);
    }
}
