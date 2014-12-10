package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.Table.EpgProgrammes.*;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.*;
import static com.nabusdev.padmedvbts2.util.Constants.*;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
        timer.scheduleAtFixedRate(timerTask, updateInterval, updateInterval);
    }

    class EpgCollectorHandler implements Runnable {
        public void run() {
            for (Adapter adapter : Adapter.getAdapterList()) {
                grabEpgToXml(adapter);
                fixUnclosedTags();
                List<Integer> pnrList = getNeededPnrList(adapter);
                List<Programme> programmes = readResultXml(pnrList);
                if (programmes.isEmpty()) {
                    String errorMessage = "Unable to get event data from multiplex. Adapter #" + adapter.getPathId();
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
                pnrList.add(channel.getPnrId());
            }
            return pnrList;
        }

        private void grabEpgToXml(Adapter adapter) {
            try {
                logger.info("Starting grabbing EPG tool...");
                String xmlPath = JAVA_EXEC_PATH + File.separator + EpgResult.XML_PATH;
                String line = TVGRAB_PATH + " --timeout 10 --adapter " + adapter.getPathId() + " --output " + xmlPath;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                executor.execute(cmdLine);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void fixUnclosedTags() {
            try {
                int counter = 0;
                Path xmlPath = Paths.get(EpgResult.XML_PATH);
                List<String> lines = Files.readAllLines(xmlPath);
                String tempFileName = EpgResult.XML_PATH + ".fixed";
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFileName));
                boolean isOpened = false;
                for (String line : lines) {
                    String trim = line.trim();
                    if (trim.startsWith("<desc")) {
                        if (!line.endsWith("</desc>")) {
                            counter++;
                            if (line.endsWith("</programme>")) {
                                line = line.replace("</programme>", "</desc></programme>");
                            } else {
                                line = line + "</desc>";
                            }
                        }
                    } else {
                        if (trim.startsWith("<programme")) {
                            if (isOpened) {
                                line = "</programme>" + line;
                                counter++;
                            } else {
                                isOpened = true;
                            }
                        } else if (trim.startsWith("</programme>")) {
                            if (isOpened) {
                                isOpened = false;
                            } else {
                                line = "<programme channel=\"0.dvb.guide\" start=\"0\" stop=\"0\">" + line;
                                counter++;
                            }
                        }
                    }
                    writer.write(line + System.lineSeparator());
                }
                writer.close();
                logger.debug("Fixed " + counter + " unclosed tags");
                File fixedFile = new File(tempFileName);
                if (fixedFile.exists()) {
                    File xmlFile = new File(EpgResult.XML_PATH);
                    if (xmlFile.exists()) xmlFile.delete();
                    fixedFile.renameTo(xmlFile);
                }
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
                                    if (!allowedPnrList.contains(channelPnr)) break allowedBreak;
                                    programme.setChannelPnrId(channelPnr);
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
                int channelPnrId = programme.getChannelPnrId();
                if (latestDates.containsKey(channelPnrId)) {
                    latestStartDate = latestDates.get(channelPnrId);
                } else {
                    latestStartDate = getLatestStartDate(programme);
                    latestDates.put(channelPnrId, latestStartDate);
                }
                //System.out.println("programme.getStart ["+programme.getStart()+"] || latestStartDate ["+latestStartDate+"]");
                if (programme.getStart() > latestStartDate) {
                    Channel channel = Channel.getByPndId(channelPnrId);
                    if (channel == null) continue;
                    int channelId = channel.getId();
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
                    String query = "INSERT INTO " + TABLE_NAME + " (" + CHANNEL_ID + "," + DATE_START + "," + DATE_STOP + "," + DATE_MAKE + "," +
                            TITLE + "," + TITLE_LANG + "," + SUBTITLE + "," + SUBTITLE_LANG + "," + DESCRIPTION + "," +
                            DESCRIPTION_LANG + "," + LANG + "," + VIDEO_ASPECT + "," + AUDIO + "," + RATING_SYSTEM + "," +
                            RATING_VALUE + "," + SUBTITLES_TYPE + "," + SUBTITLES_LANG + "," + ACTIVE + ") " +
                            "VALUES (?, ?, ?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
                    PreparedStatement statement = db.getPrepareStatement(query);
                    try {
                        int counter = 0;
                        statement.setInt(++counter, channelId);
                        statement.setTimestamp(++counter, new Timestamp(dateStart));
                        statement.setTimestamp(++counter, new Timestamp(dateStop));
                        if (title == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, title);
                        if (titleLang == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, titleLang);
                        if (subTitle == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, subTitle);
                        if (subTitleLang == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, subTitleLang);
                        if (description == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, description);
                        if (descriptionLang == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, descriptionLang);
                        if (lang == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, lang);
                        if (videoAspect == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, videoAspect);
                        if (audio == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, audio);
                        if (ratingSystem == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, ratingSystem);
                        if (ratingValue == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, ratingValue);
                        if (subtitlesType == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, subtitlesType);
                        if (subtitlesLang == null) statement.setNull(++counter, Types.NULL);
                        else statement.setString(++counter, subtitlesLang);
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
                        CHANNEL_ID + " = " + programme.getChannelId() + " ORDER BY " + DATE_START + " DESC LIMIT 1";
                ResultSet resultSet = db.selectSql(query);
                while (resultSet.next()) {
                    String result = resultSet.getString(DATE_START);
                    final String PATTERN = "yyyy-MM-dd HH:mm:ss";
                    DateFormat dateFormat = new SimpleDateFormat(PATTERN);
                    Date date = dateFormat.parse(result);
                    return date.getTime();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        private long parseEpgXmlDate(String xmlDate) {
            String target = xmlDate.split(" ")[0]; // Cutting ' +0100' from source xml date
            target = target.substring(0, target.length() - 2); // Cutting milliseconds
            final String PATTERN = "yyyyMMddHHmm";
            DateFormat dateFormat = new SimpleDateFormat(PATTERN);
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
