package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface ReadCounterTaskPort {

    Optional<Task> getTask(String taskId);

    List<Task> getTasks();

}
