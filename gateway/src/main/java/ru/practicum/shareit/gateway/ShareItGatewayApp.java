package ru.practicum.shareit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.shareit.gateway", "ru.practicum.shareit.client"})
public class ShareItGatewayApp {
    public static void main(String[] args) {
        SpringApplication.run(ShareItGatewayApp.class);
    }
}
