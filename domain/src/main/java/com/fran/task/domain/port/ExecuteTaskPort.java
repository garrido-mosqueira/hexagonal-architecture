package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;

public interface ExecuteTaskPort {

    void executeTask(Task task);
}
