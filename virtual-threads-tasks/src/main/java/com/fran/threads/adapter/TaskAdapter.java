package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import reactor.core.publisher.Flux;

import java.util.List;

public class TaskAdapter implements TaskManager {

    @Override
    public void cancelTask(String taskId) {

    }

    @Override
    public Task executeTask(Task task) {
        return null;
    }

    @Override
    public List<Task> getAllRunningCounters() {
        return null;
    }

    @Override
    public Task getRunningCounter(String counterId) {
        return null;
    }

    @Override
    public Flux<Task> startReceivingMessages() {
        return null;
    }

}
