package com.celonis.challenge.api.controllers;

import com.celonis.challenge.api.dto.ProjectGenerationTaskDTO;
import com.celonis.challenge.api.mapper.ProjectTaskMapper;
import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.tasks.counter.adapter.CounterTaskAdapter;
import com.celonis.challenge.tasks.files.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final CounterTaskAdapter counterTaskAdapter;
    private final FileService fileService;
    private final ProjectTaskMapper mapper;

    @PostMapping("/")
    public ProjectGenerationTaskDTO createTask(@RequestBody @Valid ProjectGenerationTaskDTO projectGenerationTaskDTO) {
        CounterTask counterCounterTaskJob = mapper.toDomain(projectGenerationTaskDTO);
        return mapper.toDTO(counterTaskAdapter.createTask(counterCounterTaskJob));
    }

    @GetMapping("/")
    public List<ProjectGenerationTaskDTO> listTasks() {
        return mapper.toDomain(counterTaskAdapter.getTasks());
    }

    @GetMapping("/{taskId}")
    public ProjectGenerationTaskDTO getTask(@PathVariable String taskId) {
        return mapper.toDTO(counterTaskAdapter.getTask(taskId));
    }

    @PutMapping("/{taskId}")
    public ProjectGenerationTaskDTO updateTask(@PathVariable String taskId,
                                               @RequestBody @Valid ProjectGenerationTaskDTO projectGenerationTaskDTO) {
        CounterTask counterTask = mapper.toDomain(projectGenerationTaskDTO);
        return mapper.toDTO(counterTaskAdapter.updateTask(taskId, counterTask));
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        counterTaskAdapter.cancelTask(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executeTask(@PathVariable String taskId) {
        counterTaskAdapter.executeTask(taskId);
    }

    @GetMapping("/{taskId}/result")
    public ResponseEntity<FileSystemResource> getResult(@PathVariable String taskId) {
        return fileService.getTaskResult(taskId);
    }

    @GetMapping("/running")
    public List<ProjectGenerationTaskDTO> getAllRunningCounters() {
        return mapper.toDomain(counterTaskAdapter.getAllRunningCounters());
    }

    @GetMapping("/{counterId}/progress")
    public ProjectGenerationTaskDTO getRunningCounter(@PathVariable String counterId) {
        return mapper.toDTO(counterTaskAdapter.getRunningCounter(counterId));
    }

}
