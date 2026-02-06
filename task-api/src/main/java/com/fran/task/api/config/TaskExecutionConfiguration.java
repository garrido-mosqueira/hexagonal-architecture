package com.fran.task.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fran.task.domain.port.TaskPersistencePort;
import com.fran.threads.adapter.TaskManagerAdapter;
import com.fran.threads.model.TaskThread;
import com.fran.threads.strategies.PlatformThreadingStrategy;
import com.fran.threads.strategies.ThreadingStrategy;
import com.fran.threads.strategies.VirtualThreadingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class TaskExecutionConfiguration {

    @Bean
    public RedisTemplate<String, TaskThread> tasksRegister(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TaskThread> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<TaskThread> serializer =
            new Jackson2JsonRedisSerializer<>(objectMapper, TaskThread.class);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    public List<ThreadingStrategy> threadingStrategies() {
        return Arrays.asList(
            new PlatformThreadingStrategy(),
            new VirtualThreadingStrategy()
        );
    }

    @Bean
    public TaskManagerAdapter taskManagerAdapter(RedisTemplate<String, TaskThread> tasksRegister,
                                                 List<ThreadingStrategy> threadingStrategies,
                                                 TaskPersistencePort persistencePort) {
        return new TaskManagerAdapter(tasksRegister, threadingStrategies, persistencePort);
    }

}
