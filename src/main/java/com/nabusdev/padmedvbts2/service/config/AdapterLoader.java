package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.Table.Adapters.*;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdapterLoader {
    private static Database db = DatabaseProvider.getChannelsDB();

    public static void load() {
        String query = String.format("SELECT " + ID + "," + IDENT + "," + PATH + "," + ADAPTER_TYPE + "," +
                FREQUENCY + "," + BANDWIDTH + "," + TRANSMISSION_MODE + "," + GUARD_INTERVAL + "," + HIERARCHY +
                MODULATION + " FROM " + TABLE_NAME + " WHERE " + ACTIVE + " = 1;");

        ResultSet resultSet = db.selectSql(query);
        process(resultSet);
    }

    private static void process(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt(ID);
                String ident = resultSet.getString(IDENT);
                String path = resultSet.getString(PATH);
                String adapterType = resultSet.getString(ADAPTER_TYPE);
                int frequency = resultSet.getInt(FREQUENCY);
                int bandwidth = resultSet.getInt(BANDWIDTH);
                String transmissionMode = resultSet.getString(TRANSMISSION_MODE);
                String guardInterval = resultSet.getString(GUARD_INTERVAL);
                String hierarchy = resultSet.getString(HIERARCHY);
                Adapter adapter = new Adapter(id, ident, path);
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
    }
}
