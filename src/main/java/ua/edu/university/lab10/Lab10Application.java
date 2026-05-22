package ua.edu.university.lab10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class Lab10Application {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

	public static void main(String[] args) {
		SpringApplication.run(Lab10Application.class, args);
	}
}