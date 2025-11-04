package com.fran.task.api.service;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.api.mapper.TaskCounterMapper;
import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.task.persistence.adapter.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskManager taskManager;
    private final PersistenceAdapter persistenceAdapter;
    private final TaskCounterMapper mapper;

    public TaskCounter createTask(TaskCounter taskCounter) {
        Task task = mapper.toDomain(taskCounter);
        return mapper.toDTO(persistenceAdapter.createTask(task));
    }

    public List<TaskCounter> listTasks() {
        return persistenceAdapter.getTasks().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public TaskCounter getTask(String taskId) {
        return persistenceAdapter.getTask(taskId).map(mapper::toDTO).orElseThrow(NotFoundException::new);
    }

    public TaskCounter updateTask(String taskId, TaskCounter taskCounter) {
        Task task = mapper.toDomain(taskCounter);
        return mapper.toDTO(persistenceAdapter.updateTask(taskId, task));
    }

    public void deleteTask(String taskId) {
        persistenceAdapter.deleteTask(taskId);
    }

    public void cancelTask(String taskId) {
        taskManager.cancelTask(taskId);
    }

    public void executeTask(String taskId) {
        Task task = persistenceAdapter.getTask(taskId)
            .orElseThrow(() -> new NotFoundException("Task with ID " + taskId + " not found"));
        Task executed = taskManager.executeTask(task);
        persistenceAdapter.updateExecution(executed);
    }

    public List<TaskCounter> getAllRunningCounters() {
        return mapper.toDTO(taskManager.getAllRunningCounters());
    }

    public TaskCounter getRunningCounter(String taskId) {
        Task task = persistenceAdapter.getTask(taskId)
            .orElseThrow(() -> new NotFoundException("Task with ID " + taskId + " not found"));
        return mapper.toDTO(taskManager.getRunningCounter(taskId));
    }

}
