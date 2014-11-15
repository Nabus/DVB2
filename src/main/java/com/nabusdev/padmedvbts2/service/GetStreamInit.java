package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.Main;
import com.nabusdev.padmedvbts2.model.Channel;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.File;
import java.io.PrintWriter;
import java.security.CodeSource;
import java.util.List;

public class GetStreamInit {
    private static int pid = 200;
    private static int port = 27000;
    private static final String IP = "192.168.1.7";
    private static final String CONFIG_NAME = "stream.conf";
    private static final GetStreamInit INSTANCE = new GetStreamInit();

    public static void init(List<Channel> channels) {
        INSTANCE.writeConfig(channels);
        INSTANCE.executeMuMu();
        INSTANCE.startForwarding(channels);
    }

    private void writeConfig(List<Channel> channels) {
        PrintWriter writer = null;
        try { writer = new PrintWriter(CONFIG_NAME, "UTF-8"); }
        catch (Exception e) { e.printStackTrace(); }
        if (writer == null) return;

        writer.println("card=0");
        writer.println("freq=75400");
        writer.println("bandwidth=8MHz");
        writer.println("delivery_system=DVBT");
        writer.println("unicast=1");
        writer.println("unicast_ip=" + IP);
        writer.println("unicast_port=" + port);

        for (Channel channel : channels) {
            writer.println("new_channel");
            writer.println("name=" + channel.getName());
            writer.println("ip=" + IP);
            writer.println("port=" + ++port);
            writer.println("pids=" + ++pid);
        }
        writer.close();
    }

    private void executeMuMu() {
        new Thread(new ExecuteMuMu()).start();
    }

    class ExecuteMuMu implements Runnable {
        public void run() {
            try {
                CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
                File jarFile = new File(codeSource.getLocation().toURI().getPath());
                String jarDir = jarFile.getParentFile().getPath();
                String line = "mumudvb -d -c " + jarDir + File.separator + CONFIG_NAME;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                int exitValue = executor.execute(cmdLine);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startForwarding(List<Channel> channels) {
        for (Channel channel : channels) {
            new Thread(new ForwardingService(channel)).start();
        }
    }
}
