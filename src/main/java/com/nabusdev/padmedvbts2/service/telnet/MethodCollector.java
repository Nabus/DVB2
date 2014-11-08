package com.nabusdev.padmedvbts2.service.telnet;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class MethodCollector {
    private static Map<Method, Object> classObjects = new HashMap<>();
    private static Multimap<String, Method> methods = ArrayListMultimap.create();
    private static Logger logger = LoggerFactory.getLogger(MethodCollector.class);

    public static void collect() {
        List<Class> collectedClasses = new ArrayList<>();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith("com.nabusdev.padmedvbts2.service.telnet.commands.")) {
                    final Class<?> clazz = info.load();
                    collectedClasses.add(clazz);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Class i : collectedClasses) {
            for (Method j : i.getMethods()) {
                if (j.isAnnotationPresent(TelnetCommand.class)) {
                    String commandName = j.getAnnotation(TelnetCommand.class).value();
                    methods.put(commandName, j);
                    try {classObjects.put(j, (CommandsCase) i.newInstance());}
                    catch (Exception e) {e.printStackTrace();}
                }
            }
        }
    }

    public static void execute(IoSession session) {
        for (Method method : methods.get((String)session.getAttribute("command"))) {
            if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == IoSession.class) {
                method.setAccessible(true);
                try {
                    method.invoke(classObjects.get(method), session);
                    session.setAttribute("isUnknownCommand", false);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                logger.error("TelnetCommand method " + method.getName() + " in " + method.toGenericString() + " has wrong parameters");
            }
        }
    }
}
