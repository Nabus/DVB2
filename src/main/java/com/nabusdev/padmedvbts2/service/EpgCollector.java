package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.Table.EpgProgrammes.*;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.*;
import com.nabusdev.padmedvbts2.util.Constants.Xml.EpgResult;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Programme;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;
import com.nabusdev.padmedvbts2.util.Variable;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        final int HOUR = 3600000;
        int updateInterval = HOUR;
        if (Variable.exist(EPG_UPDATE_INTERVAL)) {
            updateInterval = Integer.parseInt(Variable.get(EPG_UPDATE_INTERVAL));
        }
        timer.scheduleAtFixedRate(timerTask, HOUR, HOUR);
    }

    class EpgCollectorHandler implements Runnable {
        public void run() {
            for (Adapter adapter : Adapter.getAdapterList()) {
                grabEpgToXml(adapter);
                List<Integer> pnrList = getNeededPnrList(adapter);
                List<Programme> programmes = readResultXml(pnrList);
                if (programmes.isEmpty()) {
                    String errorMessage = "Unable to get event data from multiplex. Adapter #" + adapter.getId();
                    adapter.notifyFailOccurred(errorMessage);
                    logger.error(errorMessage);
                } else {
                    persist(programmes);
                }
            }
        }

        private List<Integer> getNeededPnrList(Adapter adapter) {
            List<Integer> pnrList = new ArrayList<>();
            for (Channel channel : adapter.getChannels()) {
                pnrList.add(channel.getId());
            }
            return pnrList;
        }

        private void grabEpgToXml(Adapter adapter) {
            try {
                String line = "tv_grab_dvb_plus --timeout 10 --adapter " + adapter.getId() + " --output " + JAVA_EXEC_PATH + File.separator + EpgResult.XML_PATH;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                executor.execute(cmdLine);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings({ "unchecked", "null" })
        private List<Programme> readResultXml(List<Integer> allowedPnrList) {
            List<Programme> programmes = new ArrayList<>();

            try {
                InputStream in = new FileInputStream(EpgResult.XML_PATH);
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
                Programme programme = null;

                while (eventReader.hasNext()) allowedBreak: {
                    XMLEvent event = eventReader.nextEvent();
                    if (event.isStartElement()) {
                        StartElement startElement = event.asStartElement();
                        String asStartElementName = event.asStartElement().getName().getLocalPart();

                        if (startElement.getName().getLocalPart().equals(EpgResult.PROGRAMME)) {
                            programme = new Programme();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(EpgResult.CHANNEL)) {
                                    String channel = attribute.getValue();
                                    int channelPnr = Integer.parseInt(channel.replace(".dvb.guide", ""));
                                    if (allowedPnrList.contains(channelPnr) == false) break allowedBreak;
                                    programme.setChannelId(channelPnr);
                                } else if (attributeName.equals(EpgResult.START)) {
                                    String startStr = attribute.getValue();
                                    long start = parseEpgXmlDate(startStr);
                                    programme.setStart(start);
                                } else if (attributeName.equals(EpgResult.STOP)) {
                                    String stopStr = attribute.getValue();
                                    long stop = parseEpgXmlDate(stopStr);
                                    programme.setStop(stop);
                                }
                            }
                        }
                        if (event.isStartElement()) {
                            if (asStartElementName.equals(EpgResult.TITLE)) {
                                event = eventReader.nextEvent();
                                String value = event.asCharacters().getData();
                                if (value != null) programme.setTitle(value);
                                Iterator<Attribute> attributes = startElement.getAttributes();
                                while (attributes.hasNext()) {
                                    Attribute attribute = attributes.next();
                                    String attributeName = attribute.getName().toString();
                                    if (attributeName.equals(EpgResult.TITLE_LANG)) {
                                        String attValue = attribute.getValue();
                                        if (attValue != null) programme.setTitleLang(attValue);
                                    }
                                }
                                continue;
                            }
                        }
                        if (asStartElementName.equals(EpgResult.SUBTITLE)) {
                            event = eventReader.nextEvent();
                            String value = event.asCharacters().getData();
                            if (value != null) programme.setSubTitle(value);
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(EpgResult.SUBTITLE_LANG)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setSubTitleLang(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(EpgResult.DESC)) {
                            event = eventReader.nextEvent();
                            String value = event.asCharacters().getData();
                            if (value != null) programme.setDesc(value);
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(EpgResult.DESC_LANG)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setDescLang(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(EpgResult.LANGUAGE)) {
                            event = eventReader.nextEvent();
                            String value = event.asCharacters().getData();
                            if (value != null) programme.setLanguage(value);
                            continue;
                        }

                        if (asStartElementName.equals(EpgResult.VIDEO)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(EpgResult.VIDEO_ASPECT)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setVideoAspect(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(EpgResult.AUDIO)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(EpgResult.AUDIO_STEREO)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setAudioStereo(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(EpgResult.RATING)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(EpgResult.RATING_SYSTEM)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setRatingSystem(attValue);
                                } else if (attributeName.equals(EpgResult.RATING_VALUE)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setRatingValue(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(EpgResult.SUBTITLES)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(EpgResult.SUBTITLES_TYPE)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setSubtitlesType(attValue);
                                } else if (attributeName.equals(EpgResult.SUBTITLES_LANGUAGE)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setSubtitlesLanguage(attValue);
                                }
                            }
                            continue;
                        }
                    }
                    if (event.isEndElement()) {
                        EndElement endElement = event.asEndElement();
                        if (endElement.getName().getLocalPart().equals(EpgResult.PROGRAMME)) {
                            programmes.add(programme);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            return programmes;
        }

        private void persist(List<Programme> programmes) {
            Map<Integer, Long> latestDates = new HashMap<>();
            for (Programme programme : programmes) {
                long latestStartDate = 0L;
                int channelId = programme.getChannelId();
                if (latestDates.containsKey(channelId)) {
                    latestStartDate = latestDates.get(channelId);
                } else {
                    latestStartDate = getLatestStartDate(programme);
                    latestDates.put(channelId, latestStartDate);
                }
                if (programme.getStart() > latestStartDate) {
                    long dateStart = programme.getStart();
                    long dateStop = programme.getStop();
                    String title = programme.getTitle();
                    String titleLang = programme.getTitleLang();
                    String subTitle = programme.getSubTitle();
                    String subTitleLang = programme.getSubTitleLang();
                    String description = programme.getDesc();
                    String descriptionLang = programme.getDescLang();
                    String lang = programme.getLanguage();
                    String videoAspect = programme.getVideoAspect();
                    String audio = programme.getAudioStereo();
                    String ratingSystem = programme.getRatingSystem();
                    String ratingValue = programme.getRatingValue();
                    String subtitlesType = programme.getSubtitlesType();
                    String subtitlesLang = programme.getSubtitlesLanguage();
                    String query = "INSERT INTO " + TABLE_NAME + "VALUES (NULL, ?, ?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
                    PreparedStatement statement = db.getPrepareStatement(query);
                    try {
                        int counter = 0;
                        statement.setLong(++counter, dateStart);
                        statement.setLong(++counter, dateStop);
                        statement.setString(++counter, title);
                        statement.setString(++counter, titleLang);
                        statement.setString(++counter, subTitle);
                        statement.setString(++counter, subTitleLang);
                        statement.setString(++counter, description);
                        statement.setString(++counter, descriptionLang);
                        statement.setString(++counter, lang);
                        statement.setString(++counter, videoAspect);
                        statement.setString(++counter, audio);
                        statement.setString(++counter, ratingSystem);
                        statement.setString(++counter, ratingValue);
                        statement.setString(++counter, subtitlesType);
                        statement.setString(++counter, subtitlesLang);
                        statement.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private long getLatestStartDate(Programme programme) {
            try {
                String query = "SELECT " + DATE_START + " FROM " + TABLE_NAME + " WHERE " +
                        CHANNEL_ID + " = " + programme.getChannelId() + " ORDER BY " + DATE_START + "DESC LIMIT 1";
                ResultSet resultSet = db.selectSql(query);
                while (resultSet.next()) {
                    final int COLUMN_INDEX = 1;
                    return resultSet.getLong(COLUMN_INDEX);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        }

        private long parseEpgXmlDate(String xmlDate) {
            String target = xmlDate.split(" ")[0]; // Cutting ' +0100' from source xml date
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddkkmmssSS");
            try {
                Date date = dateFormat.parse(target);
                long time = date.getTime();
                if (Variable.exist(EPG_TIMESTAMP_OFFSET)) {
                    time += Integer.parseInt(Variable.get(EPG_TIMESTAMP_OFFSET));
                }
                return time;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return -1;
        }
    }
}
