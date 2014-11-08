package com.nabusdev.padmedvbts2.model;
import java.util.ArrayList;
import java.util.List;

public class Adapter {
    private static List<Adapter> adapters = new ArrayList<>();
    private String path;

    Adapter(String path) {
       this.path = path;
       adapters.add(this);
    }

    public void startUse() {

    }

    public void stopUse() {

    }

    public void errorOccurred(String errorMessage) {

    }
}
