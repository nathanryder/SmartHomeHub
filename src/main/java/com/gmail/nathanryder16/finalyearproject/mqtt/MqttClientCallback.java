package com.gmail.nathanryder16.finalyearproject.mqtt;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MqttClientCallback implements MqttCallback {

    @Autowired
    private Mqtt client;

    @Autowired
    private DeviceRepository devices;

    @Autowired
    private SimpMessagingTemplate simp;

    @Override
    public void connectionLost(Throwable throwable) {
        if (client != null) {
            client.getMqttClient();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        System.out.println("Arrived: " + topic + " - " + new String(mqttMessage.getPayload()));

        List<Device> update = devices.findAllByStatusTopic(topic);
        String msg = new String(mqttMessage.getPayload());

        for (Device device : update) {
            System.out.println("Update status of: " + device.getDisplayName());
            simp.convertAndSend("/topic/updateDevices", "{'deviceID':'" + device.getDeviceID() + "', 'message':'" + msg + "'}");

            device.setLastStatus(msg);
            devices.save(device);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
