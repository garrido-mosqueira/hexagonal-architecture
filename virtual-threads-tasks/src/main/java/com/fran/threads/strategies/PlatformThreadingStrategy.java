package com.fran.threads.strategies;

import com.fran.task.domain.model.TaskType;
import org.springframework.stereotype.Component;

@Component(TaskType.Constants.PLATFORM)
public class PlatformThreadingStrategy implements ThreadingStrategy {

    @Override
    public void launch(String taskId, Runnable runnable) {
        new Thread(runnable, taskId).start();
    }

}
