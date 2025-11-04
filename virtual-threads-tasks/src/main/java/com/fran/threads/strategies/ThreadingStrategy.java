package com.fran.threads.strategies;

public interface ThreadingStrategy {

    void launch(String taskId, Runnable runnable);

}

