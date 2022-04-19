package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.CounterTask;

public interface UpdateCounterTaskPort {

    CounterTask updateTask(String taskId, CounterTask counterTaskUpdate);

}
