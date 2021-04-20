package com.gmail.nathanryder16.finalyearproject;

public enum ScriptTrigger {

    MQTT_TOPIC,
    TIME,
    PERIODIC;

    public String toFriendlyString() {
        return (name().substring(0,1) + name().substring(1).toLowerCase()).replace("_", " ");
    }

}
