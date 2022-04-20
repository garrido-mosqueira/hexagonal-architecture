package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.CounterTask;

import java.util.List;
import java.util.Optional;

public interface ReadCounterTaskPort {

    Optional<CounterTask> getTask(String taskId);

    List<CounterTask> getTasks();

}
