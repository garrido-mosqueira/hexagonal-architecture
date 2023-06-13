package com.fran.threads.config;

import com.fran.threads.model.TaskCompletableFuture;
import com.fran.threads.model.TaskFuture;
import com.fran.threads.model.TaskThread;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class TaskConfiguration {

    @Bean
    public Map<String, TaskThread> taskRegister() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, TaskFuture> taskFutureRegister() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, TaskCompletableFuture> taskCompletableFutureRegister() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

}
