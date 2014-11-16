package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.Main;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.PrintWriter;
import java.security.CodeSource;

public class StreamingService {
    private static final String HARDCODED_PORT = "27015";
    private static final String HARDCODED_IP = "192.168.1.7";
    private static final String HARDCODED_PID = "201 202 203";
    private static final String CONFIG_NAME = "stream%d.conf";
    private static final StreamingService INSTANCE = new StreamingService();
    private static Logger logger = LoggerFactory.getLogger(StreamingService.class);

    public static void init() {
        for (Adapter adapter : Adapter.getAdapterList()) {
            INSTANCE.writeConfig(adapter);
            INSTANCE.executeMuMu(adapter);
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
        writer.println("unicast_ip=" + HARDCODED_IP);
        writer.println("unicast_port=" + HARDCODED_PORT);

        for (Channel channel : adapter.getChannels()) {
            writer.println("new_channel");
            writer.println("name=" + channel.getName());
            writer.println("ip=" + HARDCODED_IP);
            writer.println("port=" + HARDCODED_PORT);
            writer.println("pids=" + HARDCODED_PID);
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
                CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
                File jarFile = new File(codeSource.getLocation().toURI().getPath());
                String jarDir = jarFile.getParentFile().getPath();
                String configName = String.format(CONFIG_NAME, adapter.getId());
                String line = "mumudvb -d -c " + jarDir + File.separator + configName;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                int exitValue = executor.execute(cmdLine);
                if (exitValue != 0) logger.error("MuMuDVB is stopping with exit code " + exitValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
