package com.gmail.nathanryder16.finalyearproject;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FinalyearprojectApplication {

	private static @Getter List<SseEmitter> emitters = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(FinalyearprojectApplication.class, args);
	}


}
