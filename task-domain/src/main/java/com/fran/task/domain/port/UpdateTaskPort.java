package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;

public interface UpdateTaskPort {

    Task updateTask(String taskId, Task taskUpdate);

}
