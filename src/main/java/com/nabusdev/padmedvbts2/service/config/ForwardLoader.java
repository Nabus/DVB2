package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.Table.StreamForward.*;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Forward;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForwardLoader {
    private static Database db = DatabaseProvider.getChannelsDB();

    public static void load() {
        String query = String.format("SELECT " + CHANNEL_ID + "," + EVENT_TYPE + "," + OUTPUT_STREAM_PROTOCOL + "," +
                OUTPUT_STREAM_HOST + "," + OUTPUT_STREAM_PORT + "," + OUTPUT_STREAM_USERNAME + "," +
                OUTPUT_STREAM_PASSWORD + "," + OUTPUT_STREAM_URL_PATH + "," + OUTPUT_STREAM_TIMEOUT + "," +
                OUTPUT_STREAM_CLIENT_LIMIT + " FROM " + TABLE_NAME + " WHERE " + ACTIVE + " = 1;");

        ResultSet resultSet = db.selectSql(query);
        linkForwardsToChannels(resultSet);
    }

    private static void linkForwardsToChannels(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                int channelId = resultSet.getInt(CHANNEL_ID);
                String eventType = resultSet.getString(EVENT_TYPE);
                String outputStreamProtocol = resultSet.getString(OUTPUT_STREAM_PROTOCOL);
                String outputStreamHost = resultSet.getString(OUTPUT_STREAM_HOST);
                int outputStreamPort = resultSet.getInt(OUTPUT_STREAM_PORT);
                String outputStreamUsername = resultSet.getString(OUTPUT_STREAM_USERNAME);
                String outputStreamPassword = resultSet.getString(OUTPUT_STREAM_PASSWORD);
                String outputStreamUrlPath = resultSet.getString(OUTPUT_STREAM_URL_PATH);
                int outputStreamTimeout = resultSet.getInt(OUTPUT_STREAM_TIMEOUT);
                int outputStreamClientLimit = resultSet.getInt(OUTPUT_STREAM_CLIENT_LIMIT);
                Channel channel = Channel.getById(channelId);
                if (channel != null) {
                    Forward forward = new Forward(channel, outputStreamProtocol, outputStreamHost, outputStreamPort);
                    forward.setEventType(eventType);
                    forward.setOutputStreamUsername(outputStreamUsername);
                    forward.setOutputStreamPassword(outputStreamPassword);
                    forward.setOutputStreamUrlPath(outputStreamUrlPath);
                    forward.setOutputStreamTimeout(outputStreamTimeout);
                    forward.setOutputStreamClientLimit(outputStreamClientLimit);
                    channel.addForward(forward);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
