package com.fran.challenge.domain.port;

import com.fran.challenge.domain.model.Task;

public interface UpdateTaskPort {

    Task updateTask(String taskId, Task taskUpdate);

}
