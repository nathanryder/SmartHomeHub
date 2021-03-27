package com.gmail.nathanryder16.finalyearproject.websocket;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.mqtt.MqttClientPublish;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
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
    @SendTo("/topic/updateDevices")
    public void update(UpdateMessage message) {
        System.out.println("REC: " + message.getDeviceID() + ":" + message.getMessage());

        Device device = deviceRepo.findByDeviceID(message.getDeviceID());
        if (device == null)
            return;

        String topic = device.getUpdateTopic() == null ? device.getStatusTopic() : device.getUpdateTopic();
        mqttClient.publish(topic, message.getMessage());
    }

}
