package de.hskl.rateme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RateMeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateMeApplication.class, args);
    }
}
