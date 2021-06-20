package de.trundicho.timeclockstamper.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("de.trundicho.timeclockstamper")
public class ClockInAndOutApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClockInAndOutApplication.class, args);
	}

}
