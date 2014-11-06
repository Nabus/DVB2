package com.nabusdev.padmedvbts2.service.telnet.commands;
import static com.nabusdev.padmedvbts2.util.Constants.Config.*;
import com.nabusdev.padmedvbts2.service.config.DatabaseLoader;
import com.nabusdev.padmedvbts2.util.Variable;

public class CONFIG extends BaseTelnetCommand {

    public boolean execute() {
        setCommandsCount(1);
        if (command.startsWith("RENEW")) return renew();
        if (command.startsWith("STATUS")) return status();
        return false;
    }

    private boolean renew() {
        DatabaseLoader.load();
        return true;
    }

    private boolean status() {
        String lastReadTimestamp = Variable.get(LAST_CONFIGURATION_READ);
        setAnswer(lastReadTimestamp);
        return true;
    }
}
