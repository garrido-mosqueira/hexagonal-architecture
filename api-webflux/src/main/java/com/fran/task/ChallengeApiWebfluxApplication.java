package com.fran.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class ChallengeApiWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChallengeApiWebfluxApplication.class, args);
    }

}
