package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;

import java.util.List;

public interface TaskExecutionPort {

    Task executeTask(Task task);

    void cancelTask(String taskId);

    List<Task> getAllRunningCounters();

    Task getRunningCounter(String counterId);

}
