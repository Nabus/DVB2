package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.Table.StreamForward.*;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Forward;
import com.nabusdev.padmedvbts2.model.ForwardStatus;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForwardLoader {
    private static Database db = DatabaseProvider.getChannelsDB();
    private static Logger logger = LoggerFactory.getLogger(ForwardLoader.class);

    public static void load() {
        String query = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = 1",
                ID, ADAPTER_CHANNEL_ID, EVENT_TYPE, OUTPUT_STREAM_PROTOCOL, OUTPUT_STREAM_HOST, OUTPUT_STREAM_PORT,
                OUTPUT_STREAM_USERNAME, OUTPUT_STREAM_PASSWORD, OUTPUT_STREAM_URL_PATH, OUTPUT_STREAM_TIMEOUT,
                OUTPUT_STREAM_CLIENT_LIMIT, TABLE_NAME, ACTIVE);
        ResultSet resultSet = db.selectSql(query);
        linkForwardsToChannels(resultSet);
    }

    private static void linkForwardsToChannels(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt(ID);
                int channelId = resultSet.getInt(ADAPTER_CHANNEL_ID);
                ForwardStatus eventType = getForwardStatus(channelId, resultSet.getString(EVENT_TYPE));
                String outputStreamProtocol = resultSet.getString(OUTPUT_STREAM_PROTOCOL);
                String outputStreamHost = resultSet.getString(OUTPUT_STREAM_HOST);
                int outputStreamPort = resultSet.getInt(OUTPUT_STREAM_PORT);
                String outputStreamUsername = resultSet.getString(OUTPUT_STREAM_USERNAME);
                String outputStreamPassword = resultSet.getString(OUTPUT_STREAM_PASSWORD);
                String outputStreamUrlPath = resultSet.getString(OUTPUT_STREAM_URL_PATH);
                int outputStreamTimeout = resultSet.getInt(OUTPUT_STREAM_TIMEOUT);
                int outputStreamClientLimit = resultSet.getInt(OUTPUT_STREAM_CLIENT_LIMIT);
                Channel channel = Channel.getById(channelId);
                boolean isChannelFoundAndActive = (channel != null);
                if (isChannelFoundAndActive) {
                    Forward forward = new Forward(id, channel, outputStreamProtocol, outputStreamHost, outputStreamPort);
                    forward.setEventType(eventType);
                    forward.setOutputStreamUsername(outputStreamUsername);
                    forward.setOutputStreamPassword(outputStreamPassword);
                    forward.setOutputStreamUrlPath(outputStreamUrlPath);
                    forward.setOutputStreamTimeout(outputStreamTimeout);
                    forward.setOutputStreamClientLimit(outputStreamClientLimit);
                    channel.addForward(forward);
                } else {
                    logger.error("Can't find active channel id " + channelId + " for forward id " + id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ForwardStatus getForwardStatus(int channelId, String eventType) {
        try {
            return ForwardStatus.valueOf(eventType);
        } catch (Exception e) {
            logger.error("Incorrect argument for column '" + EVENT_TYPE + "', Channel ID: " + channelId);
            e.printStackTrace();
        }
        return ForwardStatus.FAILED;
    }
}
