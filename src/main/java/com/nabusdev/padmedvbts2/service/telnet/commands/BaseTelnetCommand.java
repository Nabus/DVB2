package com.nabusdev.padmedvbts2.service.telnet.commands;
import com.nabusdev.padmedvbts2.service.telnet.TelnetCommand;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTelnetCommand implements TelnetCommand{
    static Logger logger = LoggerFactory.getLogger(TelnetCommand.class);
    static String argument = null;
    static IoSession session = null;
    static String command = null;
    private static String answer = null;

    public boolean perform(String paramArgument, IoSession paramSession) {
        command = paramArgument;
        argument = command;
        session = paramSession;
        return this.execute();
    }

    // Cuts specified count of first commands\words of source argument
    // var 'command' and stores result in 'argument' var
    void setCommandsCount(int count) {
        int loopCounter = 0;
        final int OFFSET = 1;
        String[] splitCommand = command.split(" ");
        StringBuilder sbArgument = new StringBuilder();
        for (String i : splitCommand){
            if (loopCounter > count - OFFSET){
                boolean isNotFirstLooping = (loopCounter != 0);
                if (isNotFirstLooping) sbArgument.append(" ");
                sbArgument.append(i);
            }
            loopCounter++;
        }
        argument = sbArgument.toString();
    }

    public String getAnswer() {
        return answer;
    }

    void setAnswer(String paramAnswer) {
        answer = paramAnswer;
    }

    boolean execute() {
        return false;
    }
}
