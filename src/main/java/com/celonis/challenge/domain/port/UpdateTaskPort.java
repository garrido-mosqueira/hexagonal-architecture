package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.Task;

public interface UpdateTaskPort {

    Task updateTask(String taskId, Task taskUpdate);

}
