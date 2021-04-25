package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.model.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class FinalyearprojectApplication {

	public static void main(String[] args) {
		Config config = new Config(true);
		if (!config.isSetup()) {
			try {
				config.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("--------------------------------------------------");
			System.out.println("config.yml has been created. MySQL connection details must be filled out before the application can start");
			System.out.println("--------------------------------------------------");
			return;
		}

		SpringApplication.run(FinalyearprojectApplication.class, args);
	}


}
