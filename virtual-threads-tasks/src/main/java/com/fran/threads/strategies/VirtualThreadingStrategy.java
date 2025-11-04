package com.fran.threads.strategies;

import org.springframework.stereotype.Component;

@Component("VIRTUAL")
public class VirtualThreadingStrategy implements ThreadingStrategy {

    @Override
    public void launch(String taskId, Runnable runnable) {
        Thread.ofVirtual().name(taskId).start(runnable);
    }

}
