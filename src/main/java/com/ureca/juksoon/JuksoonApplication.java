package com.ureca.juksoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class JuksoonApplication {

    public static void main(String[] args) {
        SpringApplication.run(JuksoonApplication.class, args);
    }

}
