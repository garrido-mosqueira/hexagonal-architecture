package com.fran.threads.strategies;

import com.fran.task.domain.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class VirtualThreadingStrategy implements ThreadingStrategy {

    @Override
    public void launch(String taskId, Runnable runnable) {
        Thread.ofVirtual().name(taskId).start(runnable);
    }

    @Override
    public TaskType supports() {
        return TaskType.VIRTUAL;
    }

}
