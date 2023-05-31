package com.fran.task.tasks.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.*;
import com.fran.task.persistence.adapter.PersistenceAdapter;
import com.fran.task.tasks.mapper.CounterMapper;
import com.fran.task.tasks.model.Counter;
import com.fran.task.tasks.service.CounterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskAdapter
        implements CreateTaskPort, ReadTaskPort, UpdateTaskPort, DeleteTaskPort, ExecuteTaskPort, CancelTaskPort {

    private final PersistenceAdapter persistenceAdapter;
    private final CounterService counterService;
    private final CounterMapper mapper;

    private final Sender sender;
    private final Receiver receiver;
    private final ObjectMapper objectMapper;

    private static final String QUEUE = "spring-reactive-queue";

    @Override
    public Task createTask(Task task) {
        Task persistedTask = persistenceAdapter.createTask(task);
        queueTask(persistedTask);
        return persistedTask;
    }

    @SneakyThrows
    private void queueTask(Task task) {
        String json = objectMapper.writeValueAsString(task);
        byte[] taskSerialized = SerializationUtils.serialize(json);
        Flux<OutboundMessage> outbound = Flux.just(new OutboundMessage("", QUEUE, taskSerialized));
        sender
                .declareQueue(QueueSpecification.queue(QUEUE))
                .thenMany(sender.sendWithPublishConfirms(outbound))
                .doOnError(exception -> log.error("Send failed", exception))
                .subscribe(m -> log.info("Message sent:" + task.getName()));
    }

    public Flux<Task> startReceivingMessages() {
        return receiver
                .consumeAutoAck(QUEUE)
                .flatMap(message -> {
                    byte[] taskSerialized = message.getBody();
                    String json = (String) SerializationUtils.deserialize(taskSerialized);
                    Task task = null;
                    try {
                        task = objectMapper.readValue(json, Task.class);
                        task.setName(task.getName() + " from the Queue");
                        task.setCreationDate(Date.from(Instant.now()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return Mono.just(task);
                });
    }

    @Override
    public List<Task> getTasks() {
        return persistenceAdapter.getTasks();
    }

    @Override
    public Optional<Task> getTask(String taskId) {
        return persistenceAdapter.getTask(taskId);
    }

    @Override
    public Task updateTask(String taskId, Task taskUpdate) {
        return persistenceAdapter.updateTask(taskId, taskUpdate);
    }

    @Override
    public void deleteTask(String taskId) {
        persistenceAdapter.deleteTask(taskId);
    }

    @Override
    public void cancelTask(String taskId) {
        counterService.cancelCounter(taskId);
    }

    public List<Task> getAllRunningCounters() {
        return mapper.toDomain(counterService.getAllRunningCounters());
    }

    @SneakyThrows
    public Task getRunningCounter(final String counterId) {
        return mapper.toDomain(counterService.getRunningCounter(counterId));
    }

    @SneakyThrows
    @Override
    public void executeTask(Task task) {
        executeCounterTask(task);
    }

    public void executeCounterTask(Task task) throws SchedulerException {
        Counter counter = mapper.toCounter(task);
        counterService.runCounterJob(counter);
        persistenceAdapter.updateExecution(task);
    }

}
