package com.fran.task.flux.service;

import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskManager;
import com.fran.task.flux.dto.ProjectGenerationTask;
import com.fran.task.flux.mapper.ProjectTaskMapper;
import com.fran.task.persistence.adapter.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final PersistenceAdapter persistenceAdapter;
    private final TaskManager taskManager;
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

    public Flux<ProjectGenerationTask> reactiveTasks() {
        return taskManager.startReceivingMessages()
                .map(mapper::toDTO);
    }

    public ProjectGenerationTask getTask(String taskId) {
        return persistenceAdapter.getTask(taskId).map(mapper::toDTO).orElseThrow(NotFoundException::new);
    }

    public ProjectGenerationTask updateTask(String taskId, ProjectGenerationTask projectGenerationTask) {
        Task task = mapper.toDomain(projectGenerationTask);
        return mapper.toDTO(persistenceAdapter.updateTask(taskId, task));
    }

    public Publisher<Void> deleteTask(String taskId) {
        persistenceAdapter.deleteTask(taskId);
        return Mono.empty();
    }

    public Publisher<Void> cancelTask(String taskId) {
        taskManager.cancelTask(taskId);
        return Mono.empty();
    }

    public Publisher<Void> executeTask(String taskId) {
        persistenceAdapter.getTask(taskId)
                .ifPresent(task -> {
                    Task executed = taskManager.executeTask(task);
                    persistenceAdapter.updateExecution(executed);
                });
        return Mono.empty();
    }

    public List<ProjectGenerationTask> getAllRunningCounters() {
        return mapper.toDTO(taskManager.getAllRunningCounters());
    }

    public ProjectGenerationTask getRunningCounter(String taskId) {
        return mapper.toDTO(taskManager.getRunningCounter(taskId));
    }

}
