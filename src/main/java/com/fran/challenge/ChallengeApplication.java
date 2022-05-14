package com.fran.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ChallengeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChallengeApplication.class, args);
    }
}
