package com.celonis.challenge.api.service;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.api.mapper.ProjectTaskMapper;
import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.tasks.adapter.TaskAdapter;
import lombok.RequiredArgsConstructor;
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

    public void deleteTask(String taskId) {
        taskAdapter.deleteTask(taskId);
    }

    public void cancelTask(String taskId) {
        taskAdapter.cancelTask(taskId);
    }

    public void executeTask(String taskId) {
        taskAdapter.getTask(taskId).ifPresent(taskAdapter::executeTask);
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
