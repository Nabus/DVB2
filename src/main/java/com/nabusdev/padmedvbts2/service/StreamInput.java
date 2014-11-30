package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Stream;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class StreamInput {
    private static int innerPort = 27000;
    private static final String innerIp = "127.0.0.1";
    private static final String CONFIG_NAME = "stream%d.conf";
    private static Map<Adapter, Executor> executors = new HashMap<>();
    private static final StreamInput INSTANCE = new StreamInput();
    private static Logger logger = LoggerFactory.getLogger(StreamInput.class);

    public static void start() {
        for (Adapter adapter : Adapter.getAdapterList()) {
            INSTANCE.writeConfig(adapter);
            INSTANCE.executeMuMu(adapter);
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
        String configName = String.format(CONFIG_NAME, adapter.getId());
        try { writer = new PrintWriter(configName, "UTF-8"); }
        catch (Exception e) { e.printStackTrace(); }
        if (writer == null) return;
        writer.println("card=" + adapter.getId());
        final int GHZ_TO_MHZ = 1000;
        int frequency = adapter.getFrequency() / GHZ_TO_MHZ;
        writer.println("freq=" + frequency);
        writer.println("bandwidth=" + adapter.getBandwidth() + "MHz");
        writer.println("delivery_system=" + adapter.getAdapterType());
        writer.println("unicast=1");
        for (Channel channel : adapter.getChannels()) {
            writer.println("channel_next");
            writer.println("name=" + channel.getName());
            writer.println("unicast_ip=" + innerIp);
            writer.println("unicast_port=" + ++innerPort);
            Stream stream = new Stream(channel, innerIp, innerPort);
            new ForwardingProcess(stream);
            channel.notifyStartUsing();
        }
        writer.close();
    }

    private void executeMuMu(Adapter adapter) {
        new Thread(new ExecuteMuMu(adapter)).start();
    }

    class ExecuteMuMu implements Runnable {
        private Adapter adapter;

        ExecuteMuMu(Adapter adapter){
            this.adapter = adapter;
        }

        public void run() {
            try {
                String configName = String.format(CONFIG_NAME, adapter.getId());
                String line = "mumudvb -d -c " + JAVA_EXEC_PATH + File.separator + configName;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
                int exitValue = executor.execute(cmdLine);
                if (exitValue != 0) logger.error("MuMuDVB is stopping with exit code " + exitValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
