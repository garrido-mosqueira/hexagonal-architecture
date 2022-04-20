package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.Task;

public interface CreateCounterTaskPort {

    Task createTask(Task task);

}
