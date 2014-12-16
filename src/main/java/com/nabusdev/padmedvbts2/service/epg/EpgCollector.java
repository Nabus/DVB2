package com.nabusdev.padmedvbts2.service.epg;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.EPG_UPDATE_INTERVAL;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Programme;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import com.nabusdev.padmedvbts2.util.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EpgCollector {
    public static EpgCollector INSTANCE = new EpgCollector();
    private static Database db = DatabaseProvider.getChannelsDB();
    private static Logger logger = LoggerFactory.getLogger(EpgCollector.class);

    public static void scheduleAll() {
        INSTANCE.setTimer();
    }

    private void setTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                new Thread(new EpgCollectorHandler()).start();
            }
        };
        final int DEFAULT_VALUE = 3600000; // 3600000 = 1 hour
        int updateInterval = DEFAULT_VALUE;
        if (Variable.exist(EPG_UPDATE_INTERVAL)) updateInterval = Integer.parseInt(Variable.get(EPG_UPDATE_INTERVAL));
        timer.scheduleAtFixedRate(timerTask, updateInterval, updateInterval);
    }

    class EpgCollectorHandler implements Runnable {
        public void run() {
            for (Adapter adapter : Adapter.getAdapterList()) {
                final String LOG_PREFIX = "[Adapter #"+ adapter.getId() +"] ";
                logger.info(LOG_PREFIX + "Starting grabbing EPG tool...");
                File epgXml = EpgGrabber.grab(adapter);
                int repaired = EpgXmlTagsRepair.repair(epgXml);
                if (repaired > 0) logger.debug(LOG_PREFIX + "Fixed " + repaired + " unclosed tags.");
                List<Programme> programmes = EpgXmlParser.parse(epgXml, adapter);

                if (programmes.isEmpty()) {
                    String errorMessage = "Unable to get event data from multiplex.";
                    adapter.notifyFailOccurred(errorMessage);
                    logger.error(LOG_PREFIX + errorMessage);
                } else {
                    int savedCount = EpgPersist.persist(programmes);
                    if (savedCount == 0) logger.info(LOG_PREFIX + "No new EPG data available to save");
                    else logger.info(LOG_PREFIX + "Successfully saved " + savedCount + " EPG programmes to DB");
                }
            }
        }
    }
}
