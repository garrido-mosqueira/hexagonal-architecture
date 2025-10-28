package com.fran.task.api.service;

import com.fran.task.api.dto.ProjectGenerationTask;
import com.fran.task.api.mapper.ProjectTaskMapper;
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

    private final TaskManager taskAdapter;
    private final PersistenceAdapter persistenceAdapter;
    private final ProjectTaskMapper mapper;

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        Task task = mapper.toDomain(projectGenerationTask);
        return mapper.toDTO(persistenceAdapter.createTask(task));
    }

    public List<ProjectGenerationTask> listTasks() {
        return persistenceAdapter.getTasks().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ProjectGenerationTask getTask(String taskId) {
        return persistenceAdapter.getTask(taskId).map(mapper::toDTO).orElseThrow(NotFoundException::new);
    }

    public ProjectGenerationTask updateTask(String taskId, ProjectGenerationTask projectGenerationTask) {
        Task task = mapper.toDomain(projectGenerationTask);
        return mapper.toDTO(persistenceAdapter.updateTask(taskId, task));
    }

    public void deleteTask(String taskId) {
        persistenceAdapter.deleteTask(taskId);
    }

    public void cancelTask(String taskId) {
        taskAdapter.cancelTask(taskId);
    }

    public void executeTask(String taskId) {
        persistenceAdapter.getTask(taskId)
                .ifPresent(task -> {
                    Task executed = taskAdapter.executeTask(task);
                    persistenceAdapter.updateExecution(executed);
                });
    }

    public List<ProjectGenerationTask> getAllRunningCounters() {
        return mapper.toDTO(taskAdapter.getAllRunningCounters());
    }

    public ProjectGenerationTask getRunningCounter(String taskId) {
        return mapper.toDTO(taskAdapter.getRunningCounter(taskId));
    }

}
