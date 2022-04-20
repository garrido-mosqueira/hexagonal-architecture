package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.Task;

public interface ExecuteTaskPort {

    void executeTask(Task task);
}
