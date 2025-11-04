package com.fran.task.domain.port;

import com.fran.task.domain.model.TaskType;

public interface TaskManagerFactory {
    TaskManager getTaskManager(TaskType taskType);
}
