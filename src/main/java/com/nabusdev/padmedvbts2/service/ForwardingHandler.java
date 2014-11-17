package com.nabusdev.padmedvbts2.service;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Client;
import com.nabusdev.padmedvbts2.model.InnerStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ForwardingHandler {
    private static final ForwardingHandler INSTANCE = new ForwardingHandler();
    private static Logger logger = LoggerFactory.getLogger(ForwardingHandler.class);

    public static void connect(Client client, Channel channel) {
        INSTANCE.connectHandler(client, channel);
    }

    public void connectHandler(Client client, Channel channel) {
        InnerStream innerStreamAddress = null;
        for (InnerStream innerStream : InnerStream.getInnerStreams()) {
            if (innerStream.getChannel() == channel) {
                innerStreamAddress = innerStream;
                break;
            }
        }
        if (innerStreamAddress == null) {
            logger.error("Can't connect client to stream");
            return;
        }

        innerStreamAddress.addClient(client);
        if (!innerStreamAddress.isForwarding()) {
            new Thread(new Forwarding(innerStreamAddress)).start();
        }
    }

    class Forwarding implements Runnable {
        private InnerStream innerStream;

        Forwarding(InnerStream innerStream) {
            this.innerStream = innerStream;
        }

        public void run() {
            try {
                URL url = new URL("http://" + innerStream.getIp() + ":" + innerStream.getPort());
                URLConnection connection = url.openConnection();
                InputStream in = connection.getInputStream();
                List<Client> listeners = innerStream.getClientList();
                List<OutputStream> outList = new ArrayList<>();
                for (Client client : listeners) {
                    outList.add(client.getDataOutputStream());
                }

                int c;
                while ((c = in.read()) != -1) {
                    for (OutputStream out : outList) {
                        out.write(c);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
