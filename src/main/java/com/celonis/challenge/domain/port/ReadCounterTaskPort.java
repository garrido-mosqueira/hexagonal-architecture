package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.CounterTask;

import java.util.List;

public interface ReadCounterTaskPort {

    CounterTask getTask(String taskId);

    List<CounterTask> getTasks();

}
