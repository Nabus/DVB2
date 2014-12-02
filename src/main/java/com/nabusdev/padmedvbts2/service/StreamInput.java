package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Stream;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamInput {
    /* TODO configurable settings Service IP, Service Port */
    private static int servicePort = 27000;
    private static final String SERVICE_IP = "127.0.0.1";
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
        String configPath = JAVA_EXEC_PATH;
        String configName = String.format(CONFIG_NAME, adapter.getId());
        try { writer = new PrintWriter(configPath + File.separator + configName, "UTF-8"); }
        catch (Exception e) { e.printStackTrace(); }
        if (writer == null) return;
        writer.println("card=" + adapter.getId());
        final int GHZ_TO_MHZ = 1000;
        int frequency = adapter.getFrequency() / GHZ_TO_MHZ;
        writer.println("freq=" + frequency);
        writer.println("bandwidth=" + adapter.getBandwidth() + "MHz");
        writer.println("delivery_system=" + adapter.getAdapterType());
        writer.println("unicast=1");
        writer.println("multicast=0");
        writer.println("multicast_ipv4=0");
        writer.println("multicast_ipv6=0");
        int port = servicePort++;
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
                String configName = String.format(CONFIG_NAME, adapter.getId());
                String line = "mumudvb -d -c " + JAVA_EXEC_PATH + File.separator + configName;
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

            /* TODO */
            final String[] toSplit = {
                    "Info:  Autoconf:",
                    "Info:  Tune:",
                    "Info:  Main:"
            };

            final String MSG_DIFFUSION_COUNT = "Diffusion 4 channels";
            final String MSG_USING_CARD = "Using DVB card \"DiBcom 7000PC\" tuner 0";
            final String MSG_TUNING_FREQ = "Tuning DVB-T to 794000000 Hz, Bandwidth: 8000000";
            final String MSG_CHANNEL_NUMBER = "Channel number :   0, name : \"Disney Channel\"  service id 1201";

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
                                logger.info("Streaming channel \"" + name + "\"");
                                int serviceId = Integer.parseInt(line.split("service id ")[0]);
                                Stream stream = new Stream();
                                stream.setServiceId(serviceId);
                                stream.setChannel(channel);
                                new ForwardingProcess(stream);
                                channel.notifyStartUsing();
                            }
                        }
                    }

                    String message = "[Adapter #" + adapter.getId() + "] " + line;
                    logger.info(message);
                    break;
                }
            }
        }

        public List<String> getLines() {
            return lines;
        }
    }
}
