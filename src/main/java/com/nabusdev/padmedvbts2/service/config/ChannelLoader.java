package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.Table.Channels.*;
import com.nabusdev.padmedvbts2.util.Constants.Table.*;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChannelLoader {
    private static Database db = DatabaseProvider.getChannelsDB();

    public static void load() {
        String query = String.format("SELECT " + ADAPTER_ID + "," + NAME + "," + IDENT + "," + PNR + "," +
                THUMB_SAVE_PATH + "," + THUMB_SAVE_FILENAME_PATTERN + "," + THUMB_SAVE_PERIOD + "," +
                THUMB_SAVE_FORMAT + " FROM " + TABLE_NAME + " WHERE " + ACTIVE + " = 1;");

        ResultSet resultSet = db.selectSql(query);
        List<Channel> channels = getChannels(resultSet);
        // Todo: process channels
    }

    private static List<Channel> getChannels(ResultSet resultSet) {
        List<Channel> channels = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int adapterId = resultSet.getInt(ADAPTER_ID);
                String name = resultSet.getString(NAME);
                String ident = resultSet.getString(IDENT);
                int pnr = resultSet.getInt(PNR);
                String thumbSavePath = resultSet.getString(THUMB_SAVE_PATH);
                String thumbSaveFilenamePattern = resultSet.getString(THUMB_SAVE_FILENAME_PATTERN);
                int thumbSavePeriod = resultSet.getInt(THUMB_SAVE_PERIOD);
                String thumbSaveFormat = resultSet.getString(THUMB_SAVE_FORMAT);
                Adapter adapter = Adapter.getById(adapterId);
                if (adapter == null) adapter = createAdapter(adapterId);
                boolean isAdapterFoundAndActive = (adapter != null);
                if (isAdapterFoundAndActive) {
                    Channel channel = new Channel(name, ident, adapter, pnr);
                    channel.setThumbPath(thumbSavePath);
                    channel.setThumbFilenamePattern(thumbSaveFilenamePattern);
                    channel.setThumbSavePeriod(thumbSavePeriod);
                    channel.setThumbSaveFormat(thumbSaveFormat);
                    channels.add(channel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channels;
    }

    private static Adapter createAdapter(int adapterId) {
        String query = String.format("SELECT " + Adapters.IDENT + "," + Adapters.PATH + "," +
                Adapters.ADAPTER_TYPE + "," + Adapters.FREQUENCY + "," + Adapters.BANDWIDTH + "," +
                Adapters.TRANSMISSION_MODE + "," + Adapters.GUARD_INTERVAL + "," + Adapters.HIERARCHY +
                Adapters.MODULATION + " FROM " + Adapters.TABLE_NAME + " WHERE " + ACTIVE + " = 1 AND id = " +
                adapterId + ";");

        ResultSet resultSet = db.selectSql(query);
        Adapter adapter = null;
        try {
            while (resultSet.next()) {
                String ident = resultSet.getString(Adapters.IDENT);
                String path = resultSet.getString(Adapters.PATH);
                String adapterType = resultSet.getString(Adapters.ADAPTER_TYPE);
                int frequency = resultSet.getInt(Adapters.FREQUENCY);
                int bandwidth = resultSet.getInt(Adapters.BANDWIDTH);
                String transmissionMode = resultSet.getString(Adapters.TRANSMISSION_MODE);
                String guardInterval = resultSet.getString(Adapters.GUARD_INTERVAL);
                String hierarchy = resultSet.getString(Adapters.HIERARCHY);
                adapter = new Adapter(adapterId, ident, path);
                adapter.setAdapterType(adapterType);
                adapter.setFrequency(frequency);
                adapter.setBandwidth(bandwidth);
                adapter.setTransmissionMode(transmissionMode);
                adapter.setGuardInterval(guardInterval);
                adapter.setHierarchy(hierarchy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adapter;
    }
}
