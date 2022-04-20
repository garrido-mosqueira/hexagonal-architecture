package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.Task;

public interface ExecuteCounterTaskPort {

    void executeTask(Task task);
}
