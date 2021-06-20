package de.trundicho.timeclockinandout.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("de.trundicho.timeclockinandout")
public class ClockInAndOutApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClockInAndOutApplication.class, args);
	}

}
