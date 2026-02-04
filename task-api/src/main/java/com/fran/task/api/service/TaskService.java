package com.fran.task.api.service;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.api.mapper.TaskCounterMapper;
import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskExecutionPort;
import com.fran.task.domain.port.TaskPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskExecutionPort executionPort;
    private final TaskPersistencePort persistencePort;
    private final TaskCounterMapper mapper;

    public TaskCounter createTask(TaskCounter taskCounter) {
        Task task = mapper.toDomain(taskCounter);
        return mapper.toDTO(persistencePort.createTask(task));
    }

    public List<TaskCounter> listTasks() {
        return persistencePort.getTasks().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public TaskCounter getTask(String taskId) {
        return persistencePort.getTask(taskId).map(mapper::toDTO).orElseThrow(NotFoundException::new);
    }

    public TaskCounter updateTask(String taskId, TaskCounter taskCounter) {
        Task task = mapper.toDomain(taskCounter);
        return mapper.toDTO(persistencePort.updateTask(taskId, task));
    }

    public void deleteTask(String taskId) {
        persistencePort.deleteTask(taskId);
    }

    public void cancelTask(String taskId) {
        executionPort.cancelTask(taskId);
    }

    public void executeTask(String taskId) {
        Task task = persistencePort.getTask(taskId)
            .orElseThrow(() -> new NotFoundException("Task with ID " + taskId + " not found"));
        Task executed = executionPort.executeTask(task);
        persistencePort.updateExecution(executed);
    }

    public List<TaskCounter> getAllRunningCounters() {
        return mapper.toDTO(executionPort.getAllRunningCounters());
    }

    public TaskCounter getRunningCounter(String taskId) {
        Task task = persistencePort.getTask(taskId)
            .orElseThrow(() -> new NotFoundException("Task with ID " + taskId + " not found"));
        return mapper.toDTO(executionPort.getRunningCounter(taskId));
    }

}
