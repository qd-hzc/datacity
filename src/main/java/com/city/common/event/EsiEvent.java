package com.city.common.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wys on 2016/1/8.
 */
public class EsiEvent {
    private String eventName;
    private Map<String, Object> args = new HashMap<>();

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
