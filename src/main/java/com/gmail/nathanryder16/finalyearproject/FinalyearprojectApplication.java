package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.cards.Card;
import com.gmail.nathanryder16.finalyearproject.cards.CardType;
import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class FinalyearprojectApplication {

	private static @Getter List<SseEmitter> emitters = new ArrayList<>();

	@Autowired
	private static DeviceRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(FinalyearprojectApplication.class, args);


//		File folder = new File("dashboards");
//		if (!folder.exists()) {
//			folder.mkdirs();
//		}
//		File dashboard = new File(folder.getPath() + File.separator + "test.yml");
//		Yaml yaml = new Yaml();
//
//		Card card = new Card(CardType.BUTTON, new Device("test.device", "TEST", "statusTopic", "method"));
//		Card card2 = new Card(CardType.BUTTON, new Device("second.test", "TWO", "statusTopic", "method"));
//
//
//		Map<String, Object> cards = new HashMap<>();
//
//		Map<String, Object> cardMap1 = new HashMap<>();
//		cardMap1.put("type", card.getType().toString());
//		cardMap1.put("id", card.getDevice().getDeviceID());
//		cards.put("1", cardMap1);
//
//		Map<String, Object> cardMap2 = new HashMap<>();
//		cardMap2.put("type", card2.getType().toString());
//		cardMap2.put("id", card2.getDevice().getDeviceID());
//		cards.put("2", cardMap2);
//
//		Map<String, Object> root = new HashMap<>();
//		root.put("dashboard", cards);
//
//
//		//Swapping
//		String from = "2";
//		String to = "1";
//		Map<String, Object> swapTo = (Map<String, Object>) cards.get(to);
//		Map<String, Object> swapFrom = (Map<String, Object>) cards.get(from);
//
//		cards.put(from, swapTo);
//		cards.put(to, swapFrom);
//		root.put("dashboard", cards);
//
//		System.out.println("Size: " + cards.size());
//
//
//		try {
//			yaml.dump(root, new PrintWriter(dashboard));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}


}
