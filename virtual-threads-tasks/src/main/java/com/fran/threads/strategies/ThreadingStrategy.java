package com.fran.threads.strategies;

@FunctionalInterface
public interface ThreadingStrategy {

    void runTask(String taskId, Runnable runnable);

}

