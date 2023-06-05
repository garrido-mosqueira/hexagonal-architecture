package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TaskManager extends
        CreateTaskPort,
        ReadTaskPort,
        UpdateTaskPort,
        DeleteTaskPort,
        ExecuteTaskPort,
        CancelTaskPort {

    List<Task> getAllRunningCounters();

    Task getRunningCounter(String counterId);

    Flux<Task> startReceivingMessages();

}
