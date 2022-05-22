package com.fran.challenge.flux.service;

import com.fran.challenge.domain.exceptions.NotFoundException;
import com.fran.challenge.domain.model.Task;
import com.fran.challenge.flux.dto.ProjectGenerationTask;
import com.fran.challenge.flux.mapper.ProjectTaskMapper;
import com.fran.challenge.tasks.adapter.TaskAdapter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
                .collect(toList());
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
        return null;
    }

    public Publisher<Void> cancelTask(String taskId) {
        taskAdapter.cancelTask(taskId);
        return null;
    }

    public Publisher<Void> executeTask(String taskId) {
        taskAdapter.getTask(taskId).ifPresent(taskAdapter::executeTask);
        return null;
    }

    public File getResult(String taskId) {
        return taskAdapter.getTaskResult(taskId);
    }

    public List<ProjectGenerationTask> getAllRunningCounters() {
        return mapper.toDTO(taskAdapter.getAllRunningCounters());
    }

    public ProjectGenerationTask getRunningCounter(String taskId) {
        return mapper.toDTO(taskAdapter.getRunningCounter(taskId));
    }

}
