package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.*;
import static com.nabusdev.padmedvbts2.util.Constants.*;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Stream;
import com.nabusdev.padmedvbts2.util.Variable;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StreamInput {
    private static Integer serviceStartPort = getServiceStartPort();
    private static final String SERVICE_IP = getServiceIp();
    private static final String CONFIG_NAME = "stream%d.conf";
    private static Map<Adapter, Executor> executors = new HashMap<>();
    private static Map<Adapter, MumuOutput> outputs = new HashMap<>();
    private static final StreamInput INSTANCE = new StreamInput();
    private static Logger logger = LoggerFactory.getLogger(StreamInput.class);

    public static void start() {
        for (Adapter adapter : Adapter.getAdapterList()) {
            INSTANCE.writeConfig(adapter);
            INSTANCE.executeMumu(adapter);
            adapter.notifyStartUsing();
        }
    }

    public static void stop() {
        for (Executor executor : executors.values()) {
            executor.getWatchdog().destroyProcess();
        }
    }

    private void writeConfig(Adapter adapter) {
        PrintWriter writer = null;
        String configName = String.format(CONFIG_NAME, adapter.getPathId());
        try { writer = new PrintWriter(JAVA_EXEC_PATH + File.separator + configName, "UTF-8"); }
        catch (Exception e) { e.printStackTrace(); }
        if (writer == null) return;
        writer.println("card=" + adapter.getPathId());
        final int GHZ_TO_MHZ = 1000;
        int frequency = adapter.getFrequency() / GHZ_TO_MHZ;
        writer.println("freq=" + frequency);
        writer.println("bandwidth=" + adapter.getBandwidth() + "MHz");
        writer.println("delivery_system=" + adapter.getAdapterType());
        writer.println("autoconfiguration=full");
        writer.println("unicast=1");
        writer.println("multicast=0");
        writer.println("multicast_ipv4=0");
        writer.println("multicast_ipv6=0");
        int port = serviceStartPort++;
        writer.println("port_http=" + port);
        writer.close();
        String ip = SERVICE_IP;
        adapter.setServiceIp(ip);
        adapter.setServicePort(port);
    }

    private void executeMumu(Adapter adapter) {
        new Thread(new ExecuteMumuHandler(adapter)).start();
    }

    class ExecuteMumuHandler implements Runnable {
        private Adapter adapter;

        ExecuteMumuHandler(Adapter adapter){
            this.adapter = adapter;
        }

        public void run() {
            try {
                String configName = String.format(CONFIG_NAME, adapter.getPathId());
                String line = MUMUDVB_PATH + " -d -c " + JAVA_EXEC_PATH + File.separator + configName;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
                MumuOutput mumuOutput = new MumuOutput();
                PumpStreamHandler streamHandler = new PumpStreamHandler(mumuOutput);
                executor.setStreamHandler(streamHandler);
                executors.put(adapter, executor);
                outputs.put(adapter, mumuOutput);
                int exitValue = executor.execute(cmdLine);
                if (exitValue != 0) logger.error("MumuDVB is stopping with exit code " + exitValue);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MumuOutput extends LogOutputStream {
        private Adapter adapter = null;
        private List<String> lines = new LinkedList<String>();

        @Override protected void processLine(String line, int level) {
            lines.add(line);

            final String[] toSplit = {
                    "Info:  Autoconf:",
                    "Info:  Tune:",
                    "Info:  Main:"
            };

            final String[] ignoreMessages = {
                    "Channel number :",
                    "Unicast : Channel"
            };

            for (String var : toSplit) {
                if (line.startsWith(var)) {
                    line = line.replace(var, "").trim();
                    if (adapter == null) {
                        for (Adapter adapter : outputs.keySet()) {
                            if (outputs.get(adapter) == this) {
                                this.adapter = adapter;
                            }
                        }
                    }

                    if (line.startsWith("Channel number :")) {
                        String name = line.split("\"")[1];
                        for (Channel channel : adapter.getChannels()) {
                            if (channel.getName().equals(name)) {
                                final String[] split = line.split(" ");
                                int serviceId = Integer.parseInt(split[split.length - 1]);
                                Stream stream = new Stream();
                                stream.setServiceId(serviceId);
                                stream.setChannel(channel);
                                stream.setPath("/bysid/" + serviceId);
                                channel.setStream(stream);
                                channel.notifyStartUsing();
                                new Thread(new ForwardingProcess(stream)).start();
                                logger.info("Streaming channel \"" + name + "\"");
                            }
                        }
                    }

                    boolean printMessage = true;
                    for (String msg : ignoreMessages) {
                        if (line.startsWith(msg)) printMessage = false;
                    }

                    if (printMessage) {
                        String message = "[Adapter #" + adapter.getPathId() + "] " + line;
                        logger.debug(message);
                    }

                    break;
                }
            }
        }

        public List<String> getLines() {
            return lines;
        }
    }

    private static String getServiceIp() {
        final String DEFAULT_SERVICE_IP = "127.0.0.1";
        if (Variable.exist(INPUT_SERVICE_IP)) return Variable.get(INPUT_SERVICE_IP);
        else return DEFAULT_SERVICE_IP;
    }

    private static int getServiceStartPort() {
        if (serviceStartPort != null) return serviceStartPort;
        final int DEFAULT_SERVICE_START_PORT = 27000;
        if (Variable.exist(INPUT_SERVICE_START_PORT)) return Integer.valueOf(Variable.get(INPUT_SERVICE_START_PORT));
        else return DEFAULT_SERVICE_START_PORT;
    }
}
