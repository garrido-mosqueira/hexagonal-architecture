package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskPersistencePort {

    Task createTask(Task task);

    List<Task> getTasks();

    Optional<Task> getTask(String taskId);

    void deleteTask(String taskId);

    Task updateTask(String taskId, Task taskUpdate);

    void updateExecution(Task task);

}
