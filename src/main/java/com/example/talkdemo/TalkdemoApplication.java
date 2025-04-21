package com.example.talkdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  
public class TalkdemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TalkdemoApplication.class, args);
    }
}
