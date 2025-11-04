package com.fran.threads.strategies;

import org.springframework.stereotype.Component;

@Component("PLATFORM")
public class PlatformThreadingStrategy implements ThreadingStrategy {

    @Override
    public void runTask(String taskId, Runnable runnable) {
        new Thread(runnable, taskId).start();
    }

}
