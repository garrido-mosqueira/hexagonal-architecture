package com.fran.threads.strategies;

import com.fran.task.domain.model.TaskType;

public class PlatformThreadingStrategy implements ThreadingStrategy {

    @Override
    public void launch(String taskId, Runnable runnable) {
        new Thread(runnable, taskId).start();
    }

    @Override
    public TaskType supports() {
        return TaskType.PLATFORM;
    }

}
