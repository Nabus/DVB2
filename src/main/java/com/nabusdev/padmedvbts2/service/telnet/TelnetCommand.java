package com.nabusdev.padmedvbts2.service.telnet;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TelnetCommand {
    String value();
}
