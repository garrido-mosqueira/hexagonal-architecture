package com.fran.threads.strategies;

import org.springframework.stereotype.Component;

@Component("VIRTUAL")
public class VirtualThreadingStrategy implements ThreadingStrategy {

    @Override
    public void runTask(String taskId, Runnable runnable) {
        Thread.ofVirtual().name(taskId).start(runnable);
    }

}
