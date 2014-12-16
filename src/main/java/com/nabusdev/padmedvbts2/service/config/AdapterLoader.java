package com.nabusdev.padmedvbts2.service.config;
import static com.nabusdev.padmedvbts2.util.Constants.Table.Adapters.*;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.util.Constants;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import com.nabusdev.padmedvbts2.util.Variable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdapterLoader {
    private static Database db = DatabaseProvider.getChannelsDB();

    public static void load() {
        String query = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s = %d AND %s = 1",
                ID, ADAPTER_TYPE, PATH, FREQUENCY, BANDWIDTH, TRANSMISSION_MODE, GUARD_INTERVAL, HIERARCHY, MODULATION,
                TABLE_NAME, SERVER_ID, DatabaseLoader.getServerId(), ACTIVE);
        ResultSet resultSet = db.selectSql(query);
        process(resultSet);
    }

    private static void process(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt(ID);
                String adapterType = resultSet.getString(ADAPTER_TYPE);
                String path = resultSet.getString(PATH);
                int frequency = resultSet.getInt(FREQUENCY);
                int bandwidth = resultSet.getInt(BANDWIDTH);
                String transmissionMode = resultSet.getString(TRANSMISSION_MODE);
                String guardInterval = resultSet.getString(GUARD_INTERVAL);
                String hierarchy = resultSet.getString(HIERARCHY);
                String modulation = resultSet.getString(MODULATION);
                Adapter adapter = new Adapter(id, path);
                adapter.setAdapterType(adapterType);
                adapter.setFrequency(frequency);
                adapter.setBandwidth(bandwidth);
                adapter.setTransmissionMode(transmissionMode);
                adapter.setGuardInterval(guardInterval);
                adapter.setHierarchy(hierarchy);
                adapter.setModulation(modulation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
