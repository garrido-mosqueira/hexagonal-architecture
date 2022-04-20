package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.CounterTask;

public interface ExecuteCounterTaskPort {

    void executeTask(CounterTask task);
}
