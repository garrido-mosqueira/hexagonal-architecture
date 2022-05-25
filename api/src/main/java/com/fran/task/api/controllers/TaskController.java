package com.fran.task.api.controllers;

import com.fran.task.api.dto.ProjectGenerationTask;
import com.fran.task.api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping("/{taskId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTask(@PathVariable String taskId) {
        service.cancelTask(taskId);
    }

    @GetMapping("/running")
    public List<ProjectGenerationTask> getAllRunningCounters() {
        return service.getAllRunningCounters();
    }

    @GetMapping("/{taskId}/progress")
    public ProjectGenerationTask getRunningCounter(@PathVariable String taskId) {
        return service.getRunningCounter(taskId);
    }

}
