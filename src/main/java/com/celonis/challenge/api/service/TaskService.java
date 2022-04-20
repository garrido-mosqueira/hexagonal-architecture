package com.celonis.challenge.api.service;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.api.mapper.ProjectTaskMapper;
import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.tasks.counter.adapter.CounterTaskAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final CounterTaskAdapter counterTaskAdapter;
    private final ProjectTaskMapper mapper;

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        Task counterTaskJob = mapper.toDomainCounter(projectGenerationTask);
        return mapper.toDTOFromCounter(counterTaskAdapter.createTask(counterTaskJob));
    }

    public List<ProjectGenerationTask> listTasks() {
        return counterTaskAdapter.getTasks().stream()
                .map(mapper::toDTOFromCounter)
                .collect(toList());
    }

    public ProjectGenerationTask getTask(String taskId) {
        return counterTaskAdapter.getTask(taskId).map(mapper::toDTOFromCounter).orElseThrow(NotFoundException::new);
    }

    public ProjectGenerationTask updateTask(String taskId, ProjectGenerationTask projectGenerationTask) {
        Task counterTaskJob = mapper.toDomainCounter(projectGenerationTask);
        return mapper.toDTOFromCounter(counterTaskAdapter.updateTask(taskId, counterTaskJob));
    }

    public void deleteTask(String taskId) {
        counterTaskAdapter.cancelTask(taskId);
    }

    public void executeTask(String taskId) {
        counterTaskAdapter.getTask(taskId).ifPresent(counterTaskAdapter::executeTask);
    }

    public File getResult(String taskId) {
        return counterTaskAdapter.getTaskResult(taskId);
    }

    public List<ProjectGenerationTask> getAllRunningCounters() {
        return mapper.toDTOFromCounter(counterTaskAdapter.getAllRunningCounters());
    }

    public ProjectGenerationTask getRunningCounter(String counterId) {
        return mapper.toDTOFromCounter(counterTaskAdapter.getRunningCounter(counterId));
    }

}
