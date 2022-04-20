package com.celonis.challenge.api.service;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.api.mapper.ProjectTaskMapper;
import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.model.FileTask;
import com.celonis.challenge.tasks.counter.adapter.CounterTaskAdapter;
import com.celonis.challenge.tasks.files.adapter.FileTaskAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final CounterTaskAdapter counterTaskAdapter;
    private final FileTaskAdapter fileTaskAdapter;
    private final ProjectTaskMapper mapper;

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        if (projectGenerationTask.getStorageLocation() != null) {
            FileTask fileTask = mapper.toDomainFile(projectGenerationTask);
            return mapper.toDTOFromFile(fileTaskAdapter.createTask(fileTask));

        }
        CounterTask counterCounterTaskJob = mapper.toDomainCounter(projectGenerationTask);
        return mapper.toDTOFromCounter(counterTaskAdapter.createTask(counterCounterTaskJob));
    }

    public List<ProjectGenerationTask> listTasks() {
        Stream<ProjectGenerationTask> taskStreamFromCounter = counterTaskAdapter.getTasks().stream()
                .map(mapper::toDTOFromCounter);

        Stream<ProjectGenerationTask> taskStreamFromFile = fileTaskAdapter.getTasks().stream()
                .map(mapper::toDTOFromFile);

        return Stream.concat(taskStreamFromCounter, taskStreamFromFile).collect(toList());
    }

    public ProjectGenerationTask getTask(String taskId) {
        Optional<CounterTask> counterTask = counterTaskAdapter.getTask(taskId);
        if (counterTask.isPresent()) {
            return counterTask.map(mapper::toDTOFromCounter).orElseThrow(NotFoundException::new);
        }
        return fileTaskAdapter.getTask(taskId).map(mapper::toDTOFromFile).orElseThrow(NotFoundException::new);
    }

    public ProjectGenerationTask updateTask(String taskId, ProjectGenerationTask projectGenerationTask) {
        if (projectGenerationTask.getStorageLocation() != null) {
            FileTask fileTask = mapper.toDomainFile(projectGenerationTask);
            return mapper.toDTOFromFile(fileTaskAdapter.updateTask(taskId, fileTask));
        }
        CounterTask counterCounterTaskJob = mapper.toDomainCounter(projectGenerationTask);
        return mapper.toDTOFromCounter(counterTaskAdapter.updateTask(taskId, counterCounterTaskJob));
    }

    public void deleteTask(String taskId) {
        counterTaskAdapter.cancelTask(taskId);
        fileTaskAdapter.deleteTask(taskId);
    }

    public void executeTask(String taskId) {
        counterTaskAdapter.getTask(taskId).ifPresent(counterTaskAdapter::executeTask);
        fileTaskAdapter.getTask(taskId).ifPresent(fileTaskAdapter::executeTask);
    }

    public File getResult(String taskId) {
        return fileTaskAdapter.getTaskResult(taskId);
    }

    public List<ProjectGenerationTask> getAllRunningCounters() {
        return mapper.toDTOFromCounter(counterTaskAdapter.getAllRunningCounters());
    }

    public ProjectGenerationTask getRunningCounter(String counterId) {
        return mapper.toDTOFromCounter(counterTaskAdapter.getRunningCounter(counterId));
    }
}
