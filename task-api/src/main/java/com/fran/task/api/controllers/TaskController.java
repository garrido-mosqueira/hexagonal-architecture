package com.fran.task.api.controllers;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskCounter createTask(@RequestBody TaskCounter taskCounter) {
        return service.createTask(taskCounter);
    }

    @GetMapping("/")
    public List<TaskCounter> listTasks() {
        return service.listTasks();
    }

    @GetMapping("/{taskId}")
    public TaskCounter getTask(@PathVariable String taskId) {
        return service.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskCounter updateTask(@PathVariable String taskId,
                                  @RequestBody TaskCounter taskCounter) {
        return service.updateTask(taskId, taskCounter);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        service.deleteTask(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void executeTask(@PathVariable String taskId) {
        service.executeTask(taskId);
    }

    @PostMapping("/{taskId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancelTask(@PathVariable String taskId) {
        service.cancelTask(taskId);
    }

    @GetMapping("/running")
    public List<TaskCounter> getAllRunningCounters() {
        return service.getAllRunningCounters();
    }

    @GetMapping("/{taskId}/progress")
    public TaskCounter getRunningCounter(@PathVariable String taskId) {
        return service.getRunningCounter(taskId);
    }

}
