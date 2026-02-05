package com.fran.task.api.service;

import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskExecutionPort;
import com.fran.task.domain.port.TaskPersistencePort;
import com.fran.task.domain.port.TaskUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService implements TaskUseCase {

    private final TaskExecutionPort executionPort;
    private final TaskPersistencePort persistencePort;

    @Override
    public Task createTask(Task task) {
        return persistencePort.createTask(task);
    }

    @Override
    public List<Task> listTasks() {
        return persistencePort.getTasks();
    }

    @Override
    public Optional<Task> getTask(String taskId) {
        return persistencePort.getTask(taskId);
    }

    @Override
    public Task updateTask(String taskId, Task task) {
        return persistencePort.updateTask(taskId, task);
    }

    @Override
    public void deleteTask(String taskId) {
        persistencePort.deleteTask(taskId);
    }

    @Override
    public void cancelTask(String taskId) {
        executionPort.cancelTask(taskId);
    }

    @Override
    public void executeTask(String taskId) {
        Task task = getTask(taskId)
            .orElseThrow(() -> new NotFoundException("Task with ID " + taskId + " not found"));
        executionPort.executeTask(task);
    }

    @Override
    public List<Task> getAllRunningCounters() {
        return executionPort.getAllRunningCounters();
    }

    @Override
    public Task getRunningCounter(String taskId) {
        getTask(taskId).orElseThrow(() -> new NotFoundException("Task with ID " + taskId + " not found"));
        return executionPort.getRunningCounter(taskId);
    }

}
