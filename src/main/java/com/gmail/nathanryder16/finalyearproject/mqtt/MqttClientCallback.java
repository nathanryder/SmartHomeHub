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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            String status = null;
            if (device.getStatusPattern() != null) {
                String template = device.getStatusPattern().replace("%s", "(\\w+)").replace("\"", "\\\"");
                template = "(.*)" + template.replace("{", "\\{").replace("}", "\\}") + "(.*)";

                Pattern pattern = Pattern.compile(template);
                Matcher matcher = pattern.matcher(msg);

                if (matcher.matches()) {
                    status = matcher.group(2);
                }
            }



            if (status == null) {
                if (!msg.startsWith("{")) {
                    msg = "\"" + msg + "\"";
                }
            } else {
                msg = "\"" + status + "\"";
            }
            device.setLastStatus(msg);
            devices.save(device);

            msg = msg.replace(device.getInactivePayload(), "OFF").replace(device.getActivePayload(), "ON");

            System.out.println("Update: " + msg);
            simp.convertAndSend("/topic/update/" + device.getDeviceID(), "{\"deviceID\":\"" + device.getDeviceID() + "\", \"message\":" + msg + "}");


        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
