package com.gmail.nathanryder16.finalyearproject.mqtt;

import com.gmail.nathanryder16.finalyearproject.ScriptTrigger;
import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.model.Script;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import com.gmail.nathanryder16.finalyearproject.repository.ScriptRepository;
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
    private ScriptRepository scriptRepo;

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

            String status = null;
            if (device.getStatusPattern() != null) {
                String template = device.getStatusPattern().replace("%s", "([a-zA-Z0-9\\.]+)").replace("\"", "\\\"");
                template = "(.*)" + template.replace("{", "\\{").replace("}", "\\}") + "(.*)";

                System.out.println("T: " + template);
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

        //Check if script wants to trigger based on MQTT_TOPIC
        List<Script> scripts = scriptRepo.findByTriggerType(ScriptTrigger.MQTT_TOPIC);
        for (Script script : scripts) {
            if (script.getTriggerValue().equals(topic)) {
                script.run(msg);
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
