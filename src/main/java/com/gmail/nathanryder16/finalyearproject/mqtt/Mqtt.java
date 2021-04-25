package com.gmail.nathanryder16.finalyearproject.mqtt;

import com.gmail.nathanryder16.finalyearproject.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Mqtt {

    @Qualifier("mqttClientPublish")
    @Autowired
    private MqttClientPublish mqttClient;

    @Autowired
    private Config config;

    @Bean
    public MqttClientPublish getMqttClient() {
        String host = "tcp://" + config.getMqttHost() +":" + config.getMqttPort();
        String clientId = "smartHub";

        mqttClient.connect(host, clientId, config.getMqttUser(), config.getMqttPassword());
        mqttClient.subscribe("#");

        return mqttClient;
    }

}
