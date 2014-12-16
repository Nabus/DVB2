package com.nabusdev.padmedvbts2.service.epg;
import com.nabusdev.padmedvbts2.util.Constants.Table.*;
import com.nabusdev.padmedvbts2.model.Channel;
import com.nabusdev.padmedvbts2.model.Programme;
import com.nabusdev.padmedvbts2.util.Database;
import com.nabusdev.padmedvbts2.util.DatabaseProvider;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpgPersist {
    private static Database db = DatabaseProvider.getChannelsDB();

    protected static int persist(List<Programme> programmes) {
        int savedCount = 0;
        Map<Integer, Long> latestDates = new HashMap<>();
        for (Programme programme : programmes) {
            long latestStartDate = 0L;
            int channelPnrId = programme.getChannelPnr();
            if (latestDates.containsKey(channelPnrId)) {
                latestStartDate = latestDates.get(channelPnrId);
            } else {
                latestStartDate = getLatestStartDate(channelPnrId);
                latestDates.put(channelPnrId, latestStartDate);
            }

            if (programme.getStart() > latestStartDate) {
                Channel channel = Channel.getByPndId(channelPnrId);
                if (channel != null) try {
                    executeMainQuery(programme);
                    executeTitleQueries(programme);
                    executeDescQueries(programme);
                    executeCategoryQueries(programme);
                    executeCreditQueries(programme);
                    savedCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return savedCount;
    }

    private static void executeMainQuery(Programme programme) throws Exception {
        int premiereVal = 0, lastChanceVal = 0, newVal = 0;
        if (programme.isPremiere()) premiereVal = 1;
        if (programme.isLastChance()) lastChanceVal = 1;
        if (programme.isNew()) newVal = 1;

        String query = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                "VALUES (?, ?, ?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                EpgProgrammes.TABLE_NAME, EpgProgrammes.CHANNEL_ID, EpgProgrammes.DATE_START, EpgProgrammes.DATE_STOP,
                EpgProgrammes.DATE_MAKE, EpgProgrammes.AUDIO, EpgProgrammes.VIDEO_ASPECT, EpgProgrammes.VIDEO_QUALITY,
                EpgProgrammes.SUBTITLES_TYPE, EpgProgrammes.SUBTITLES_LANG, EpgProgrammes.RATING_SYSTEM,
                EpgProgrammes.RATING_VALUE, EpgProgrammes.ORIG_LANG, EpgProgrammes.LENGTH, EpgProgrammes.ICON,
                EpgProgrammes.URL, EpgProgrammes.COUNTRY, EpgProgrammes.EPISODE_NUM, EpgProgrammes.STAR_RATING,
                EpgProgrammes.PREVIOUSLY_SHOWN, EpgProgrammes.PREMIERE, EpgProgrammes.LAST_CHANCE, EpgProgrammes.NEW);

        int counter = 0;
        PreparedStatement statement = db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(++counter, programme.getChannel().getId());
        statement.setLong(++counter, programme.getStart());
        statement.setLong(++counter, programme.getStop());
        if (programme.getAudioStereo() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getAudioStereo());
        if (programme.getVideoAspect() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getVideoAspect());
        if (programme.getVideoQuality() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getVideoQuality());
        if (programme.getSubtitlesType() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getSubtitlesType());
        if (programme.getSubtitlesLanguage() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getSubtitlesLanguage());
        if (programme.getRatingSystem() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getRatingSystem());
        if (programme.getRatingValue() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getRatingValue());
        if (programme.getOrigLanguage() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getOrigLanguage());
        statement.setInt(++counter, programme.getLength());
        if (programme.getIcon() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getIcon());
        if (programme.getUrl() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getUrl());
        if (programme.getCountry() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getCountry());
        statement.setInt(++counter, programme.getEpisodeNum());
        if (programme.getStarRating() == null) statement.setNull(++counter, Types.NULL);
        else statement.setString(++counter, programme.getStarRating());
        statement.setLong(++counter, programme.getPreviouslyShown());
        statement.setInt(++counter, premiereVal);
        statement.setInt(++counter, lastChanceVal);
        statement.setInt(++counter, newVal);
        statement.executeUpdate();

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                programme.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Failed to obtain ID of INSERT Programme query");
            }
        }
    }

    private static void executeTitleQueries(Programme programme) throws Exception {
        final int TYPE_TITLE = 0;
        final int TYPE_SUBTITLE = 1;
        String query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, '?', '?', ?)",
                EpgProgrammeTitle.TABLE_NAME, EpgProgrammeTitle.EPG_PROGRAMME_ID, EpgProgrammeTitle.LANG_NAME,
                EpgProgrammeTitle.VAL, EpgProgrammeTitle.SUB);

        for (Map.Entry<String, String> titlePair : programme.getTitles()) {
            int counter = 0;
            PreparedStatement statement = db.getPrepareStatement(query);
            statement.setInt(++counter, programme.getId());
            statement.setString(++counter, titlePair.getValue());
            statement.setString(++counter, titlePair.getKey());
            statement.setInt(++counter, TYPE_TITLE);
            db.execSql(query);
        }
        for (Map.Entry<String, String> subtitlePair : programme.getSubtitles()) {
            int counter = 0;
            PreparedStatement statement = db.getPrepareStatement(query);
            statement.setInt(++counter, programme.getId());
            statement.setString(++counter, subtitlePair.getValue());
            statement.setString(++counter, subtitlePair.getKey());
            statement.setInt(++counter, TYPE_SUBTITLE);
            db.execSql(query);
        }
    }

    private static void executeDescQueries(Programme programme) throws Exception {
        for (Map.Entry<String, String> descPair : programme.getDescriptions()) {
            String descLang = descPair.getValue();
            String descValue = descPair.getKey();
            String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%s, '%s', '%s')",
                    EpgProgrammeDesc.TABLE_NAME, EpgProgrammeDesc.EPG_PROGRAMME_ID, EpgProgrammeDesc.LANG_NAME,
                    EpgProgrammeDesc.VAL, programme.getId(), descLang, descValue);
            db.execSql(query);
        }
    }

    private static void executeCategoryQueries(Programme programme) throws Exception {
        for (Map.Entry<String, String> categoryPair : programme.getCategories()) {
            String categoryLang = categoryPair.getValue();
            String categoryName = categoryPair.getKey();
            String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%s, '%s', '%s')",
                    EpgProgrammeCategory.TABLE_NAME, EpgProgrammeCategory.EPG_PROGRAMME_ID,
                    EpgProgrammeCategory.LANG_NAME, EpgProgrammeCategory.VAL,
                    programme.getId(), categoryLang, categoryName);
            db.execSql(query);
        }
    }

    private static void executeCreditQueries(Programme programme) throws Exception {
        for (Map.Entry<String, String> creditsPair : programme.getCredits()) {
            String creditType = creditsPair.getValue();
            String creditValue = creditsPair.getKey();
            String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (%s, '%s', '%s')",
                    EpgProgrammeCredit.TABLE_NAME, EpgProgrammeCredit.EPG_PROGRAMME_ID, EpgProgrammeCredit.CREDIT_TYPE,
                    EpgProgrammeCredit.VAL, programme.getId(), creditType, creditValue);
            db.execSql(query);
        }
    }

    private static long getLatestStartDate(int channelPnrId) {
        try {
            Channel channel = Channel.getByPndId(channelPnrId);
            if (channel != null) {
                String query = String.format("SELECT %s FROM %s WHERE %s = %d ORDER BY %s DESC LIMIT 1",
                        EpgProgrammes.DATE_START, EpgProgrammes.TABLE_NAME, EpgProgrammes.CHANNEL_ID,
                        channel.getId(), EpgProgrammes.DATE_START);
                ResultSet resultSet = db.selectSql(query);
                while (resultSet.next()) {
                    String result = resultSet.getString(EpgProgrammes.DATE_START);
                    final String PATTERN = "yyyy-MM-dd HH:mm:ss";
                    DateFormat dateFormat = new SimpleDateFormat(PATTERN);
                    Date date = dateFormat.parse(result);
                    return date.getTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
