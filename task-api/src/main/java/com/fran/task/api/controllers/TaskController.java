package com.fran.task.api.controllers;

import com.fran.task.api.dto.TaskCounter;
import com.fran.task.api.mapper.TaskCounterMapper;
import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.port.TaskUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskUseCase useCase;
    private final TaskCounterMapper mapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskCounter createTask(@RequestBody TaskCounter taskCounter) {
        return mapper.toDTO(useCase.createTask(mapper.toDomain(taskCounter)));
    }

    @GetMapping("/")
    public List<TaskCounter> listTasks() {
        return mapper.toDTO(useCase.listTasks());
    }

    @GetMapping("/{taskId}")
    public TaskCounter getTask(@PathVariable String taskId) {
        return useCase.getTask(taskId).map(mapper::toDTO).orElseThrow(NotFoundException::new);
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskCounter updateTask(@PathVariable String taskId,
                                  @RequestBody TaskCounter taskCounter) {
        return mapper.toDTO(useCase.updateTask(taskId, mapper.toDomain(taskCounter)));
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        useCase.deleteTask(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void executeTask(@PathVariable String taskId) {
        useCase.executeTask(taskId);
    }

    @PostMapping("/{taskId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancelTask(@PathVariable String taskId) {
        useCase.cancelTask(taskId);
    }

    @GetMapping("/running")
    public List<TaskCounter> getAllRunningCounters() {
        return mapper.toDTO(useCase.getAllRunningCounters());
    }

    @GetMapping("/{taskId}/progress")
    public TaskCounter getRunningCounter(@PathVariable String taskId) {
        return mapper.toDTO(useCase.getRunningCounter(taskId));
    }

}
