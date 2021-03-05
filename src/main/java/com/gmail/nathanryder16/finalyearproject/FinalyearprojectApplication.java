package com.gmail.nathanryder16.finalyearproject;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinalyearprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalyearprojectApplication.class, args);

		MqttClient client = null;
		try {
			client = new MqttClient("tcp://192.168.15.3:1883", MqttClient.generateClientId());
			MqttConnectOptions opts = new MqttConnectOptions();
			opts.setUserName("mosquitto");
			opts.setPassword("mosquittopasswd".toCharArray());

			client.connect(opts);
			client.subscribe("#", new MQTTCallback());

			MqttMessage msg = new MqttMessage();
			msg.setPayload("Hub started".getBytes());
			client.publish("hub/broadcast", msg);


		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

}
