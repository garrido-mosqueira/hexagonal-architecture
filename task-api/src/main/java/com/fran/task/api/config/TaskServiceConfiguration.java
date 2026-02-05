package com.fran.task.api.config;

import com.fran.task.application.service.TaskService;
import com.fran.task.domain.port.TaskExecutionPort;
import com.fran.task.domain.port.TaskPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskServiceConfiguration {

    @Bean
    public TaskService taskService(TaskExecutionPort executionPort, TaskPersistencePort persistencePort) {
        return new TaskService(executionPort, persistencePort);
    }

}
