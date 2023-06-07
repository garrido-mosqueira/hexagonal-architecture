package com.fran.threads.config;

import com.fran.task.domain.model.Task;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TaskConfiguration {

    @Bean
    public Map<String, Task> taskRegister() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ExecutorService executorService (){
        return Executors.newCachedThreadPool();
    }

}
