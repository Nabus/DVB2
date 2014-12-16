package com.nabusdev.padmedvbts2.service.epg;
import static com.nabusdev.padmedvbts2.util.Constants.Variables.*;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Programme;
import com.nabusdev.padmedvbts2.util.Variable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EpgXmlParser {

    protected static List<Programme> parse(File xmlFile, Adapter adapter) {
        List<Integer> allowedPnrList = getNeededPnrList(adapter);
        List<Programme> programmes = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            Element root = document.getDocumentElement();
            NodeList programmeList = root.getElementsByTagName("programme");
            for (int i = 0; i < programmeList.getLength(); i++) {
                try {
                    Element programmeElement = (Element) programmeList.item(i);
                    String channel = programmeElement.getAttribute("channel");
                    String start = programmeElement.getAttribute("start");
                    String stop = programmeElement.getAttribute("stop");

                    /* For optimization purposes it's better to do this check immediately after receipt of the channelPnr */
                    int channelPnr = Integer.parseInt(channel.replace(".dvb.guide", ""));
                    if (!allowedPnrList.contains(channelPnr)) continue;

                    List<Map.Entry<String, String>> titles = new ArrayList<>();
                    NodeList titleList = programmeElement.getElementsByTagName("title");
                    for (int j = 0; j < titleList.getLength(); j++) {
                        Element titleElement = (Element) titleList.item(j);
                        String titleContent = titleElement.getTextContent();
                        String titleLang = titleElement.getAttribute("lang");
                        titles.add(new AbstractMap.SimpleEntry<>(titleContent, titleLang));
                    }

                    List<Map.Entry<String, String>> subtitles = new ArrayList<>();
                    NodeList subtitleList = programmeElement.getElementsByTagName("sub-title");
                    for (int j = 0; j < subtitleList.getLength(); j++) {
                        Element subtitleElement = (Element) subtitleList.item(j);
                        String subtitleContent = subtitleElement.getTextContent();
                        String subtitleLang = subtitleElement.getAttribute("lang");
                        subtitles.add(new AbstractMap.SimpleEntry<>(subtitleContent, subtitleLang));
                    }

                    List<Map.Entry<String, String>> descriptions = new ArrayList<>();
                    NodeList descList = programmeElement.getElementsByTagName("desc");
                    for (int j = 0; j < descList.getLength(); j++) {
                        Element descElement = (Element) descList.item(j);
                        String descContent = descElement.getTextContent();
                        String descLang = descElement.getAttribute("lang");
                        descriptions.add(new AbstractMap.SimpleEntry<>(descContent, descLang));
                    }

                    List<Map.Entry<String, String>> credits = new ArrayList<>();
                    NodeList creditsList = programmeElement.getElementsByTagName("credits");
                    for (int j = 0; j < creditsList.getLength(); j++) {
                        Element creditsElement = (Element) creditsList.item(j);
                        String creditContent = creditsElement.getTextContent();
                        String creditType = creditsElement.getAttribute("type");
                        credits.add(new AbstractMap.SimpleEntry<>(creditContent, creditType));
                    }

                    List<Map.Entry<String, String>> categories = new ArrayList<>();
                    NodeList categoryList = programmeElement.getElementsByTagName("category");
                    for (int j = 0; j < categoryList.getLength(); j++) {
                        Element categoryElement = (Element) categoryList.item(j);
                        String categoryContent = categoryElement.getTextContent();
                        String categoryLang = categoryElement.getAttribute("lang");
                        categories.add(new AbstractMap.SimpleEntry<>(categoryContent, categoryLang));
                    }

                    String stereoContent = null;
                    NodeList audioList = programmeElement.getElementsByTagName("audio");
                    Element audioElement = (Element) audioList.item(0);
                    if (audioElement != null) {
                        NodeList stereoList = audioElement.getElementsByTagName("stereo");
                        Element stereoElement = (Element) stereoList.item(0);
                        if (stereoElement != null) stereoContent = stereoElement.getTextContent();
                    }

                    String videoAspect = null;
                    String videoQuality = null;
                    NodeList videoList = programmeElement.getElementsByTagName("video");
                    Element videoElement = (Element) videoList.item(0);
                    if (videoElement != null) {
                        NodeList aspectList = videoElement.getElementsByTagName("aspect");
                        Element aspectElement = (Element) aspectList.item(0);
                        if (aspectElement != null) videoAspect = aspectElement.getTextContent();
                        NodeList qualityList = videoElement.getElementsByTagName("quality");
                        Element qualityElement = (Element) qualityList.item(0);
                        if (qualityElement != null) videoQuality = qualityElement.getTextContent();
                    }

                    String subtitlesType = null;
                    String subtitlesLanguage = null;
                    NodeList subtitlesList = programmeElement.getElementsByTagName("subtitles");
                    Element subtitlesElement = (Element) subtitlesList.item(0);
                    if (subtitlesElement != null) {
                        subtitlesType = subtitlesElement.getAttribute("type");
                        NodeList subtitlesLanguageList = subtitlesElement.getElementsByTagName("language");
                        Element typeElement = (Element) subtitlesLanguageList.item(0);
                        if (typeElement != null) subtitlesLanguage = typeElement.getTextContent();
                    }

                    String ratingSystem = null;
                    String ratingValue = null;
                    NodeList ratingList = programmeElement.getElementsByTagName("rating");
                    Element ratingElement = (Element) ratingList.item(0);
                    if (ratingElement != null) {
                        ratingSystem = ratingElement.getAttribute("system");
                        NodeList valueList = ratingElement.getElementsByTagName("value");
                        Element valueElement = (Element) valueList.item(0);
                        if (valueElement != null) ratingValue = valueElement.getTextContent();
                    }

                    String language = null;
                    NodeList languageList = programmeElement.getElementsByTagName("language");
                    Element languageElement = (Element) languageList.item(0);
                    if (languageElement != null) language = languageElement.getTextContent();

                    String origLanguage = null;
                    NodeList origLanguageList = programmeElement.getElementsByTagName("orig-language");
                    Element origLanguageElement = (Element) origLanguageList.item(0);
                    if (origLanguageElement != null) origLanguage = origLanguageElement.getTextContent();

                    String length = null;
                    NodeList lengthList = programmeElement.getElementsByTagName("length");
                    Element lengthElement = (Element) lengthList.item(0);
                    if (lengthElement != null) length = lengthElement.getTextContent();

                    String icon = null;
                    NodeList iconList = programmeElement.getElementsByTagName("icon");
                    Element iconElement = (Element) iconList.item(0);
                    if (iconElement != null) icon = iconElement.getTextContent();

                    String url = null;
                    NodeList urlList = programmeElement.getElementsByTagName("url");
                    Element urlElement = (Element) urlList.item(0);
                    if (urlElement != null) url = urlElement.getTextContent();

                    String country = null;
                    NodeList countryList = programmeElement.getElementsByTagName("country");
                    Element countryElement = (Element) countryList.item(0);
                    if (countryElement != null) country = countryElement.getTextContent();

                    String episodeNum = null;
                    NodeList episodeNumList = programmeElement.getElementsByTagName("episode-num");
                    Element episodeNumElement = (Element) episodeNumList.item(0);
                    if (episodeNumElement != null) episodeNum = episodeNumElement.getTextContent();

                    String starRating = null;
                    NodeList starRatingList = programmeElement.getElementsByTagName("star-rating");
                    Element starRatingElement = (Element) starRatingList.item(0);
                    if (starRatingElement != null) starRating = starRatingElement.getTextContent();

                    String previouslyShown = null;
                    NodeList previouslyShownList = programmeElement.getElementsByTagName("previously-shown");
                    Element previouslyShownElement = (Element) previouslyShownList.item(0);
                    if (previouslyShownElement != null) previouslyShown = previouslyShownElement.getTextContent();

                    boolean isPremiere = false;
                    NodeList premiereList = programmeElement.getElementsByTagName("premiere");
                    if (((Element) premiereList.item(0)) != null) isPremiere = true;

                    boolean isLastChance = false;
                    NodeList lastChanceList = programmeElement.getElementsByTagName("last-chance");
                    if (((Element) lastChanceList.item(0)) != null) isLastChance = true;

                    boolean isNew = false;
                    NodeList newList = programmeElement.getElementsByTagName("new");
                    if (((Element) newList.item(0)) != null) isNew = true;

                    Programme programme = new Programme();
                    programme.setChannelPnr(channelPnr);
                    Channel programmeChannel = Channel.getByPndId(channelPnr);
                    if (programmeChannel == null) continue;
                    programme.setChannel(programmeChannel);
                    programme.setStart(parseEpgXmlDate(start));
                    programme.setStop(parseEpgXmlDate(stop));
                    programme.setTitles(titles);
                    programme.setSubtitles(subtitles);
                    programme.setDescriptions(descriptions);
                    programme.setCredits(credits);
                    programme.setCategories(categories);
                    programme.setAudioStereo(stereoContent);
                    programme.setVideoAspect(videoAspect);
                    programme.setVideoQuality(videoQuality);
                    programme.setSubtitlesType(subtitlesType);
                    programme.setSubtitlesLanguage(subtitlesLanguage);
                    programme.setRatingSystem(ratingSystem);
                    programme.setRatingValue(ratingValue);
                    programme.setLanguage(language);
                    programme.setOrigLanguage(origLanguage);
                    programme.setLength(Integer.parseInt(length));
                    programme.setIcon(icon);
                    programme.setUrl(url);
                    programme.setCountry(country);
                    programme.setEpisodeNum(Integer.parseInt(episodeNum));
                    programme.setStarRating(starRating);
                    programme.setPreviouslyShown(Long.parseLong(previouslyShown));
                    programme.setPremiere(isPremiere);
                    programme.setLastChance(isLastChance);
                    programme.setNew(isNew);
                    programmes.add(programme);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return programmes;
    }

    private static List<Integer> getNeededPnrList(Adapter adapter) {
        List<Integer> pnrList = new ArrayList<>();
        for (Channel channel : adapter.getChannels()) {
            pnrList.add(channel.getPnrId());
        }
        return pnrList;
    }

    private static long parseEpgXmlDate(String xmlDate) {
        final String PATTERN = "yyyyMMddHHmm";
        String target = xmlDate.split(" ")[0]; // Cutting ' +0100' from source xml date
        target = target.substring(0, target.length() - 2); // Cutting milliseconds
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
