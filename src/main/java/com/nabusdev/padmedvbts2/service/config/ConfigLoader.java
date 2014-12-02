package com.nabusdev.padmedvbts2.service.config;

import com.nabusdev.padmedvbts2.util.Variable;

public abstract class ConfigLoader {

    static void setVariable(String key, String value) {
        Variable.set(key, value);
    }
}
