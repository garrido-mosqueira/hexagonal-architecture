package com.fran.task;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestContainerConfiguration {

    static {
        System.setProperty("DOCKER_HOST", "unix:///var/run/docker.sock");
        System.setProperty("DOCKER_API_VERSION", "1.44");
    }

    @Container
    static final MongoDBContainer mongoDb = new MongoDBContainer(DockerImageName.parse("mongo:7.0.5"));

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setMongoDbContainerProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDb::getReplicaSetUrl);
        registry.add("spring.data.mongodb.host", mongoDb::getHost);
        registry.add("spring.data.mongodb.port", mongoDb::getFirstMappedPort);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

}
