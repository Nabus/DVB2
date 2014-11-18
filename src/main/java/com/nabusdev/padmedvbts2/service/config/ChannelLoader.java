package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.Table.Channels.*;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChannelLoader {
    private static Database db = DatabaseProvider.getChannelsDB();
    private static Logger logger = LoggerFactory.getLogger(ChannelLoader.class);

    public static void load() {
        String query = String.format("SELECT " + ID + "," + ADAPTER_ID + "," + NAME + "," + IDENT + "," + PNR + "," +
                THUMB_SAVE_PATH + "," + THUMB_SAVE_FILENAME_PATTERN + "," + THUMB_SAVE_PERIOD + "," +
                THUMB_SAVE_FORMAT + " FROM " + TABLE_NAME + " WHERE " + ACTIVE + " = 1;");

        ResultSet resultSet = db.selectSql(query);
        linkChannelsToAdapters(resultSet);
    }

    private static void linkChannelsToAdapters(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt(ID);
                int adapterId = resultSet.getInt(ADAPTER_ID);
                String name = resultSet.getString(NAME);
                String ident = resultSet.getString(IDENT);
                int pnr = resultSet.getInt(PNR);
                String thumbSavePath = resultSet.getString(THUMB_SAVE_PATH);
                String thumbSaveFilenamePattern = resultSet.getString(THUMB_SAVE_FILENAME_PATTERN);
                int thumbSavePeriod = resultSet.getInt(THUMB_SAVE_PERIOD);
                String thumbSaveFormat = resultSet.getString(THUMB_SAVE_FORMAT);
                Adapter adapter = Adapter.getById(adapterId);
                boolean isAdapterFoundAndActive = (adapter != null);
                if (isAdapterFoundAndActive) {
                    Channel channel = new Channel(id, name, ident, adapter, pnr);
                    channel.setThumbPath(thumbSavePath);
                    channel.setThumbFilenamePattern(thumbSaveFilenamePattern);
                    channel.setThumbSavePeriod(thumbSavePeriod);
                    channel.setThumbSaveFormat(thumbSaveFormat);
                    adapter.addChannel(channel);
                } else {
                    logger.error("Can't find active adapter id " + adapterId + "for channel id " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
