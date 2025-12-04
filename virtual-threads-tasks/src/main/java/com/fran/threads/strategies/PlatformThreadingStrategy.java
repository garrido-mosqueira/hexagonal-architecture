package com.fran.threads.strategies;

import com.fran.task.domain.model.TaskType;
import org.springframework.stereotype.Component;

@Component
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
