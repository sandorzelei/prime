package com.intland.prime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableCaching
@EnableScheduling
@ComponentScan
@SpringBootApplication
public class PrimeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(PrimeApplication.class, args);
    }

}
