package com.fran.task.domain.port;

import com.fran.task.domain.model.Task;

public interface CreateTaskPort {

    Task createTask(Task task);

}
