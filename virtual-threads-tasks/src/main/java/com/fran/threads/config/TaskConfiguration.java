package com.fran.threads.config;

import com.fran.threads.model.TaskVirtualThread;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TaskConfiguration {

    @Bean
    public Map<String, TaskVirtualThread> taskRegister() {
        return new ConcurrentHashMap<>();
    }

}
