package com.gmail.nathanryder16.finalyearproject.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttClientPublish {

    @Autowired
    private MqttClientCallback callback;

    private static MqttClient client;

    private static MqttClient getMqttClient() {
        return client;
    }

    private static void setClient(MqttClient client) {
        MqttClientPublish.client = client;
    }

    public void connect(String host, String clientId, String username, String password) {
        MqttClient client;

        try {
            client = new MqttClient(host, clientId, new MemoryPersistence());
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            opts.setUserName(username);
            opts.setPassword(password.toCharArray());
            opts.setKeepAliveInterval(100);
            opts.setConnectionTimeout(100);

            MqttClientPublish.setClient(client);

            client.setCallback(callback);
            client.connect(opts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        MqttMessage msg = new MqttMessage();
        msg.setPayload(message.getBytes());

        MqttTopic mqttTopic = MqttClientPublish.getMqttClient().getTopic(topic);

        MqttDeliveryToken delivery;
        try {
            delivery = mqttTopic.publish(msg);
            delivery.waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            MqttClientPublish.getMqttClient().subscribe(topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
