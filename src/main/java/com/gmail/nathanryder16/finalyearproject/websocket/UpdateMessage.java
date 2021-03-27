package com.gmail.nathanryder16.finalyearproject.websocket;

import lombok.Getter;
import lombok.Setter;

public class UpdateMessage {

    private @Getter @Setter String deviceID;
    private @Getter @Setter String message;

    public UpdateMessage() {

    }

    public UpdateMessage(String deviceID, String message) {
        this.deviceID = deviceID;
        this.message = message;
    }
}
