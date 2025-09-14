package com.shanthifarms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MilkDeliveryApp {
    public static void main(String[] args) {
        SpringApplication.run(MilkDeliveryApp.class, args);
    }
}
