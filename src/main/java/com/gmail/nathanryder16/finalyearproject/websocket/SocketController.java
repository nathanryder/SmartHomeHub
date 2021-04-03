package com.gmail.nathanryder16.finalyearproject.websocket;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.mqtt.MqttClientPublish;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import org.aspectj.bridge.IMessageContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @Qualifier("mqttClientPublish")
    @Autowired
    private MqttClientPublish mqttClient;

    @Autowired
    private DeviceRepository deviceRepo;

    @MessageMapping("/socket")
//    @SendTo("/topic/updateDevices")
    public void update(UpdateMessage message) {
        Device device = deviceRepo.findByDeviceID(message.getDeviceID());
        if (device == null)
            return;

        String payload = message.getMessage();
        String topic = device.getUpdateTopic() == null ? device.getStatusTopic() : device.getUpdateTopic();

        if (device.getUpdatePattern() != null) {
            payload = device.getUpdatePattern().replace("%s", message.getMessage());
            System.out.println("Changed message!");
        }
        if (device.getActivePayload() != null) {
            payload = payload.replace("ON", device.getActivePayload());
        }
        if (device.getInactivePayload() != null) {
            payload = payload.replace("OFF", device.getInactivePayload());
        }

        mqttClient.publish(topic, payload);
    }

}
