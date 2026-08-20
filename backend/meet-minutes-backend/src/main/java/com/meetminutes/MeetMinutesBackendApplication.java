package com.meetminutes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MeetMinutesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetMinutesBackendApplication.class, args);
    }

}
