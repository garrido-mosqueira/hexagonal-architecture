package com.fran.threads.strategies;

import com.fran.task.domain.model.TaskType;

public interface ThreadingStrategy {

    void launch(String taskId, Runnable runnable);
    TaskType supports();

}

