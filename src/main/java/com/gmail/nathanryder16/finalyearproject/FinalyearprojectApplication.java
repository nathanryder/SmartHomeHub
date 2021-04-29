package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.model.Config;
import com.gmail.nathanryder16.finalyearproject.model.Script;
import com.gmail.nathanryder16.finalyearproject.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class FinalyearprojectApplication {

	@Autowired
	private ScriptRepository scriptRepo;

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

	@Bean
	public void timer() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextMin = now.withSecond(0).plusMinutes(1);
		long delay = Duration.between(now, nextMin).toMillis();

		Timer minute = new Timer();
		minute.schedule(new TimerTask() {
			@Override
			public void run() {

				List<Script> scripts = scriptRepo.findByTriggerType(ScriptTrigger.TIME);
				for (Script script : scripts) {
					String[] triggerTime = script.getTriggerValue().split(":");
					LocalDateTime ldt = LocalDateTime.now();

					if (ldt.getHour() == Integer.parseInt(triggerTime[0]) &&
							ldt.getMinute() == Integer.parseInt(triggerTime[1])) {

						script.run();
					}
				}

			}
		}, delay, 60000);
	}


}
