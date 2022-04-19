package com.celonis.challenge.api.service;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.api.mapper.ProjectTaskMapper;
import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.model.FileTask;
import com.celonis.challenge.tasks.counter.adapter.CounterTaskAdapter;
import com.celonis.challenge.tasks.files.adapter.FileTaskAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final CounterTaskAdapter counterTaskAdapter;
    private final FileTaskAdapter fileTaskAdapter;
    private final ProjectTaskMapper mapper;

    public ProjectGenerationTask createTask(ProjectGenerationTask projectGenerationTask) {
        if (projectGenerationTask.getStorageLocation().isEmpty()) {
            CounterTask counterCounterTaskJob = mapper.toDomainCounter(projectGenerationTask);
            return mapper.toDTOFromCounter(counterTaskAdapter.createTask(counterCounterTaskJob));
        }
        FileTask fileTask = mapper.toDomainFile(projectGenerationTask);
        return mapper.toDTOFromFile(fileTaskAdapter.createTask(fileTask));
    }

    public List<ProjectGenerationTask> listTasks() {
        return mapper.toDTOFromCounter(counterTaskAdapter.getTasks());
    }

    public ProjectGenerationTask getTask(String taskId) {
        return mapper.toDTOFromCounter(counterTaskAdapter.getTask(taskId));
    }

    public ProjectGenerationTask updateTask(String taskId, ProjectGenerationTask projectGenerationTask) {
        CounterTask counterTask = mapper.toDomainCounter(projectGenerationTask);
        return mapper.toDTOFromCounter(counterTaskAdapter.updateTask(taskId, counterTask));
    }

    public void deleteTask(String taskId) {
        counterTaskAdapter.cancelTask(taskId);
    }

    public void executeTask(String taskId) {
        counterTaskAdapter.executeTask(taskId);
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
