package com.nabusdev.padmedvbts2.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class Variable {
    private static Map<String, String> variables = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(Variable.class);

    public static String get(String key) {
        String variable = variables.get(key);
        if (variable == null) logger.warn("Returning value for variable '" + key + "' is null.");
        return variables.get(key);
    }

    public static void add(String value, String key) {
        variables.put(value, key);
    }

    public static boolean exist(String key) {
        return variables.containsKey(key);
    }
}
