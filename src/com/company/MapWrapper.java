package com.company;

import java.util.HashMap;

/**
 * Created by michaelplott on 10/4/16.
 */
public class MapWrapper {
    public HashMap<String, User> userWrapper = new HashMap<>();

    public MapWrapper() {
    }

    public HashMap<String, User> getUserWrapper() {
        return userWrapper;
    }
}
