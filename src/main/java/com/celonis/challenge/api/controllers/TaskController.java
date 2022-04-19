package com.celonis.challenge.api.controllers;

import com.celonis.challenge.api.dto.ProjectGenerationTask;
import com.celonis.challenge.api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    @PostMapping("/")
    public ProjectGenerationTask createTask(@RequestBody @Valid ProjectGenerationTask projectGenerationTask) {
        return service.createTask(projectGenerationTask);
    }

    @GetMapping("/")
    public List<ProjectGenerationTask> listTasks() {
        return service.listTasks();
    }

    @GetMapping("/{taskId}")
    public ProjectGenerationTask getTask(@PathVariable String taskId) {
        return service.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    public ProjectGenerationTask updateTask(@PathVariable String taskId,
                                            @RequestBody @Valid ProjectGenerationTask projectGenerationTask) {
        return service.updateTask(taskId, projectGenerationTask);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        service.deleteTask(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executeTask(@PathVariable String taskId) {
        service.executeTask(taskId);
    }

    @GetMapping("/{taskId}/result")
    public ResponseEntity<FileSystemResource> getResult(@PathVariable String taskId) {
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");
        File fileResult = service.getResult(taskId);
        return new ResponseEntity<>(new FileSystemResource(fileResult), respHeaders, HttpStatus.OK);
    }

    @GetMapping("/running")
    public List<ProjectGenerationTask> getAllRunningCounters() {
        return service.getAllRunningCounters();
    }

    @GetMapping("/{counterId}/progress")
    public ProjectGenerationTask getRunningCounter(@PathVariable String counterId) {
        return service.getRunningCounter(counterId);
    }

}
