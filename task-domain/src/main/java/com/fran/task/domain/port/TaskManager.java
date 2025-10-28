package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TaskManager extends
        ExecuteTaskPort,
        CancelTaskPort {

    List<Task> getAllRunningCounters();

    Task getRunningCounter(String counterId);

    Flux<Task> startReceivingMessages();

}
