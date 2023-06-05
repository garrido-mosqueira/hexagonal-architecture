package com.fran.task.flux.service;

import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.model.Task;
import com.fran.task.flux.dto.ProjectGenerationTask;
import com.fran.task.flux.mapper.ProjectTaskMapper;
import com.fran.task.tasks.adapter.TaskAdapter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskAdapter taskAdapter;
    private final ProjectTaskMapper mapper;

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        Task task = mapper.toDomain(projectGenerationTask);
        return mapper.toDTO(taskAdapter.createTask(task));
    }

    public List<ProjectGenerationTask> listTasks() {
        return taskAdapter.getTasks().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public Flux<ProjectGenerationTask> reactiveTasks() {
        return taskAdapter.startReceivingMessages()
                .map(mapper::toDTO);
    }

    public ProjectGenerationTask getTask(String taskId) {
        return taskAdapter.getTask(taskId).map(mapper::toDTO).orElseThrow(NotFoundException::new);
    }

    public ProjectGenerationTask updateTask(String taskId, ProjectGenerationTask projectGenerationTask) {
        Task task = mapper.toDomain(projectGenerationTask);
        return mapper.toDTO(taskAdapter.updateTask(taskId, task));
    }

    public Publisher<Void> deleteTask(String taskId) {
        taskAdapter.deleteTask(taskId);
        return Mono.empty();
    }

    public Publisher<Void> cancelTask(String taskId) {
        taskAdapter.cancelTask(taskId);
        return Mono.empty();
    }

    public Publisher<Void> executeTask(String taskId) {
        taskAdapter.getTask(taskId).ifPresent(taskAdapter::executeTask);
        return Mono.empty();
    }

    public List<ProjectGenerationTask> getAllRunningCounters() {
        return mapper.toDTO(taskAdapter.getAllRunningCounters());
    }

    public ProjectGenerationTask getRunningCounter(String taskId) {
        return mapper.toDTO(taskAdapter.getRunningCounter(taskId));
    }

}
