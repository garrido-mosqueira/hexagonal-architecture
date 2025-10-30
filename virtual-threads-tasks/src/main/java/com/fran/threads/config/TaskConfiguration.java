package com.fran.threads.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fran.threads.model.TaskVirtualThread;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class TaskConfiguration {

    @Bean
    public RedisTemplate<String, TaskVirtualThread> tasksRegister(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TaskVirtualThread> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Configure ObjectMapper for LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Use Jackson2JsonRedisSerializer with a specific type
        Jackson2JsonRedisSerializer<TaskVirtualThread> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, TaskVirtualThread.class);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

}
