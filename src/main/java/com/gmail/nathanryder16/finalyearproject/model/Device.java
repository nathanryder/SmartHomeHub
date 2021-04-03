package com.gmail.nathanryder16.finalyearproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    private @Getter @Setter String deviceID;
    private @Getter @Setter String displayName;
    private @Getter @Setter String statusTopic;
    private @Getter @Setter String method;
    private @Getter @Setter String updateTopic;
    private @Getter @Setter String activePayload;
    private @Getter @Setter String inactivePayload;
    private @Setter String lastStatus = "";
    private @Getter @Setter String statusPattern;
    private @Getter @Setter String updatePattern;

    public Device(String deviceID, String displayName, String statusTopic, String method) {
        this.deviceID = deviceID;
        this.displayName = displayName;
        this.statusTopic = statusTopic;
        this.method = method;
    }

    public Device() {

    }

    public String getLastStatus() {
        return lastStatus.replace("\"", "").replace(getActivePayload(), "ON").replace(getInactivePayload(), "OFF");
    }

}
