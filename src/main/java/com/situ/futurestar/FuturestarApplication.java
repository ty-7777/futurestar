package com.situ.futurestar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FuturestarApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuturestarApplication.class, args);
    }

}
