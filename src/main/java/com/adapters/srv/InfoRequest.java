package com.adapters.srv;

import com.google.common.collect.Maps;

import java.util.Map;

public class InfoRequest {
    private String topic;
    private Map<String, Object> options=Maps.newHashMap();

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }
}

