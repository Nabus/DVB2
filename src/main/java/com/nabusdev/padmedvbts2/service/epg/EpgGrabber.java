package com.nabusdev.padmedvbts2.service.epg;
import static com.nabusdev.padmedvbts2.util.Constants.JAVA_EXEC_PATH;
import static com.nabusdev.padmedvbts2.util.Constants.TVGRAB_PATH;
import com.nabusdev.padmedvbts2.model.Adapter;
import com.nabusdev.padmedvbts2.util.Constants;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class EpgGrabber {
    private static Logger logger = LoggerFactory.getLogger(EpgGrabber.class);

    protected static File grab(Adapter adapter) {
        try {
            String xmlPath = JAVA_EXEC_PATH + File.separator + Constants.Xml.EpgResult.XML_PATH;
            String line = TVGRAB_PATH + " --timeout 10 --adapter " + adapter.getPathId() + " --output " + xmlPath;
            CommandLine cmdLine = CommandLine.parse(line);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(cmdLine);
            return new File(xmlPath);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
