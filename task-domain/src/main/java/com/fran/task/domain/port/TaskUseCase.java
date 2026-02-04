package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskUseCase {

    Task createTask(Task task);

    List<Task> listTasks();

    Optional<Task> getTask(String taskId);

    Task updateTask(String taskId, Task task);

    void deleteTask(String taskId);

    void executeTask(String taskId);

    void cancelTask(String taskId);

    List<Task> getAllRunningCounters();

    Task getRunningCounter(String taskId);

}
