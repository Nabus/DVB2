package com.nabusdev.padmedvbts2.service;
import static com.nabusdev.padmedvbts2.util.Constants.Xml.EpgResult.*;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Programme;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EpgCollector {
    public static EpgCollector INSTANCE = new EpgCollector();
    private static Logger logger = LoggerFactory.getLogger(EpgCollector.class);

    public static void collect() {
        INSTANCE.collectHandler();
    }

    private void collectHandler() {
        new Thread(new EpgCollectorHandler()).start();
    }

    class EpgCollectorHandler implements Runnable {

        public void run() {
            grabEpgToXml();
            List<Integer> pnrList = getNeededPnrList();
            List<Programme> programmes = readResultXml(pnrList);
            if (programmes.isEmpty()) {
                logger.error("Unable to get event data from multiplex");
            } else {
                // How many adapters need to scan EPG?
                // How to persist to DB epg info?
            }
        }

        private List<Integer> getNeededPnrList() {
            List<Integer> pnrList = new ArrayList<>();
            for (Channel channel : Channel.getChannelList()) {
                pnrList.add(channel.getId());
            }
            return pnrList;
        }

        private void grabEpgToXml() {
            try {
                String line = "tv_grab_dvb_plus --timeout 10 --adapter 0 --output " + JAVA_EXEC_PATH + File.separator + XML_PATH;
                CommandLine cmdLine = CommandLine.parse(line);
                DefaultExecutor executor = new DefaultExecutor();
                executor.execute(cmdLine);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings({ "unchecked", "null" })
        private List<Programme> readResultXml(List<Integer> allowedPnrList) {
            List<Programme> programmes = new ArrayList<Programme>();

            try {
                InputStream in = new FileInputStream(XML_PATH);
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
                Programme programme = null;

                while (eventReader.hasNext()) allowedBreak: {
                    XMLEvent event = eventReader.nextEvent();
                    if (event.isStartElement()) {
                        StartElement startElement = event.asStartElement();
                        String asStartElementName = event.asStartElement().getName().getLocalPart();

                        if (startElement.getName().getLocalPart().equals(PROGRAMME)) {
                            programme = new Programme();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(CHANNEL)) {
                                    String channel = attribute.getValue();
                                    int channelPnr = Integer.parseInt(channel.replace(".dvb.guide", ""));
                                    if (allowedPnrList.contains(channelPnr) == false) break allowedBreak;
                                    programme.setChannelId(channelPnr);
                                } else if (attributeName.equals(START)) {
                                    String startStr = attribute.getValue();
                                    long start = Long.parseLong(startStr.split(" ")[0]);
                                    programme.setStart(start);
                                } else if (attributeName.equals(STOP)) {
                                    String stopStr = attribute.getValue();
                                    long stop = Long.parseLong(stopStr.split(" ")[0]);
                                    programme.setStop(stop);
                                }
                            }
                        }
                        if (event.isStartElement()) {
                            if (asStartElementName.equals(TITLE)) {
                                event = eventReader.nextEvent();
                                String value = event.asCharacters().getData();
                                if (value != null) programme.setTitle(value);
                                Iterator<Attribute> attributes = startElement.getAttributes();
                                while (attributes.hasNext()) {
                                    Attribute attribute = attributes.next();
                                    String attributeName = attribute.getName().toString();
                                    if (attributeName.equals(TITLE_LANG)) {
                                        String attValue = attribute.getValue();
                                        if (attValue != null) programme.setTitleLang(attValue);
                                    }
                                }
                                continue;
                            }
                        }
                        if (asStartElementName.equals(SUBTITLE)) {
                            event = eventReader.nextEvent();
                            String value = event.asCharacters().getData();
                            if (value != null) programme.setSubTitle(value);
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(SUBTITLE_LANG)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setSubTitleLang(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(DESC)) {
                            event = eventReader.nextEvent();
                            String value = event.asCharacters().getData();
                            if (value != null) programme.setDesc(value);
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(DESC_LANG)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setDescLang(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(LANGUAGE)) {
                            event = eventReader.nextEvent();
                            String value = event.asCharacters().getData();
                            if (value != null) programme.setLanguage(value);
                            continue;
                        }

                        if (asStartElementName.equals(VIDEO)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(VIDEO_ASPECT)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setVideoAspect(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(AUDIO)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(AUDIO_STEREO)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setAudioStereo(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(RATING)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(RATING_SYSTEM)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setRatingSystem(attValue);
                                } else if (attributeName.equals(RATING_VALUE)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setRatingValue(attValue);
                                }
                            }
                            continue;
                        }
                        if (asStartElementName.equals(SUBTITLES)) {
                            event = eventReader.nextEvent();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                Attribute attribute = attributes.next();
                                String attributeName = attribute.getName().toString();
                                if (attributeName.equals(SUBTITLES_TYPE)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setSubtitlesType(attValue);
                                } else if (attributeName.equals(SUBTITLES_LANGUAGE)) {
                                    String attValue = attribute.getValue();
                                    if (attValue != null) programme.setSubtitlesLanguage(attValue);
                                }
                            }
                            continue;
                        }
                    }
                    if (event.isEndElement()) {
                        EndElement endElement = event.asEndElement();
                        if (endElement.getName().getLocalPart().equals(PROGRAMME)) {
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
    }
}
