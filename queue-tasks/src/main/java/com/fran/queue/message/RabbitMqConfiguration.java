package com.fran.queue.message;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        return connectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(ConnectionFactory connectionFactory) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
        cachingConnectionFactory.setConnectionLimit(10); // Set the maximum number of connections in the pool
        return cachingConnectionFactory;
    }

    @Bean
    public Mono<Connection> connectionMono(ConnectionFactory connectionFactory) {
        return Mono.fromCallable(connectionFactory::newConnection);
    }

    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono) {
        return new SenderOptions().connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }

    @Bean
    public ReceiverOptions receiverOptions(Mono<Connection> connectionMono) {
        return new ReceiverOptions().connectionMono(connectionMono);
    }

    @Bean
    Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }

}
