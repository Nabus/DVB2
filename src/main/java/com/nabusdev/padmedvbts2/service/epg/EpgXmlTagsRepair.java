package com.nabusdev.padmedvbts2.service.epg;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EpgXmlTagsRepair {

    protected static int repair(File file) {
        try {
            int lineNum = 0;
            int counter = 0;
            Path xmlPath = file.toPath();
            Path fixedXmlPath = new File(xmlPath.toString() + ".fixed").toPath();

            FileWriter fixedXml = new FileWriter(fixedXmlPath.toFile());
            List<String> lines = Files.readAllLines(xmlPath);
            BufferedWriter writer = new BufferedWriter(fixedXml);
            boolean isDescOpened = false;

            for (String line : lines) {
                lineNum++;
                if (lineNum == 1 || lineNum == 2) continue;
                String trim = line.trim();

                if (trim.contains("<desc")) isDescOpened = true;
                if (trim.contains("</desc>")) isDescOpened = false;
                if (trim.contains("</programme>")) {
                    if (isDescOpened) {
                        line.replace("</programme>", "</desc></programme>");
                        counter++;
                    }
                }

                if (trim.contains("<desc") && trim.contains("</programme>")) {
                    line = "</programme>";
                }

                writer.write(line + System.lineSeparator());
            }
            writer.close();
            if (fixedXmlPath.toFile().exists()) {
                if (xmlPath.toFile().exists()) xmlPath.toFile().delete();
                fixedXmlPath.toFile().renameTo(xmlPath.toFile());
            }
            return counter;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
