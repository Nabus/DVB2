package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.*;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import static com.nabusdev.padmedvbts2.util.Constants.Table.StreamThroughput.*;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import com.nabusdev.padmedvbts2.util.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

public class ThroughputService {
    final int RECORD_SEC_COUNT = 10;
    private static Logger logger = LoggerFactory.getLogger(ThroughputService.class);
    private static final ThroughputService INSTANCE = new ThroughputService();
    private static Database db = DatabaseProvider.getChannelsDB();

    public static void scheduleAll() {
        for (Channel channel : Channel.getChannelList()) {
            schedule(channel);
        }
    }

    public static void schedule(Channel channel) {
        INSTANCE.setTimer(channel);
    }

    private void setTimer(final Channel channel) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                checkThroughput(channel);
            }
        };
        final int DEFAULT_INTERVAL = 300000; // 300000 - five min
        int updateInterval = DEFAULT_INTERVAL;
        if (Variable.exist(THROUGHPUT_UPDATE_INTERVAL)) {
            updateInterval = Integer.parseInt(Variable.get(THROUGHPUT_UPDATE_INTERVAL));
        }
        timer.scheduleAtFixedRate(timerTask, updateInterval, updateInterval);
    }

    private void checkThroughput(Channel channel) {
        File file = recordStreamToFile(channel);
        if (file != null) {
            int kbpsValue = (int) calculateSize(file);
            persistResults(channel, kbpsValue);
        }
    }

    private File recordStreamToFile(Channel channel) {
        final int MILLIS_FACTOR = 1000;
        final String THROUGHPUT_FILE_PATH = JAVA_EXEC_PATH + File.separator + "throughput.video";
        File file = StreamRecorder.record(THROUGHPUT_FILE_PATH, channel, RECORD_SEC_COUNT * MILLIS_FACTOR);
        return file;
    }

    private long calculateSize(File file) {
        final long BYTE_TO_KBPS_MULTIPLIER = 125;
        return (file.length() * BYTE_TO_KBPS_MULTIPLIER) / RECORD_SEC_COUNT;
    }

    private void persistResults(Channel channel, int kbpsValue) {
        if (kbpsValue > 0) {
            String query;
            if (isChannelAlreadyInDB(channel)) {
                query = String.format("UPDATE %s SET %s = %d WHERE %s = %d",
                        TABLE_NAME, VAL, kbpsValue, ADAPTER_CHANNEL_ID, channel.getId());
            } else {
                query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%d, %d, now())",
                        TABLE_NAME, ADAPTER_CHANNEL_ID, VAL, DATE_CREATED, channel.getId(), kbpsValue);
            }
            db.execSql(query);
        } else {
            logger.debug("Failed to calculate throughput of channel (" + channel.getId() + "|" + channel.getName() + ")");
        }
    }

    private boolean isChannelAlreadyInDB(Channel channel) {
        String query = String.format("SELECT %s FROM %s WHERE %s = %d",
                ADAPTER_CHANNEL_ID, TABLE_NAME, ADAPTER_CHANNEL_ID, channel.getId());
        ResultSet resultSet = db.selectSql(query);
        try {
            while (resultSet.next()) {
                int channelId = resultSet.getInt(ADAPTER_CHANNEL_ID);
                if (channelId > 0) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
