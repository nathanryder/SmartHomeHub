package com.gmail.nathanryder16.finalyearproject.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Mqtt {

    @Qualifier("mqttClientPublish")
    @Autowired
    private MqttClientPublish mqttClient;

    @Bean
    public MqttClientPublish getMqttClient() {
        String host = "tcp://192.168.15.3:1883";
        String clientId = "smartHub";
        String username = "mosquitto";
        String password = "mosquittopasswd";

        mqttClient.connect(host, clientId, username, password);
        mqttClient.subscribe("#");

        return mqttClient;
    }

}
