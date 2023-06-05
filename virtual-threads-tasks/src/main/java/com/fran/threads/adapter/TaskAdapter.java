package com.fran.threads.adapter;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

public class TaskAdapter implements TaskManager {

    @Override
    public void cancelTask(String taskId) {

    }

    @Override
    public Task createTask(Task task) {
        return null;
    }

    @Override
    public void deleteTask(String taskId) {

    }

    @Override
    public void executeTask(Task task) {

    }

    @Override
    public Optional<Task> getTask(String taskId) {
        return Optional.empty();
    }

    @Override
    public List<Task> getTasks() {
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

    @Override
    public Task updateTask(String taskId, Task taskUpdate) {
        return null;
    }
}
