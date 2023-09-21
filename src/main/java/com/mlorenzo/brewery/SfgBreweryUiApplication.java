package com.mlorenzo.brewery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SfgBreweryUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SfgBreweryUiApplication.class, args);
    }

}

