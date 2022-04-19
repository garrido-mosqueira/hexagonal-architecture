package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.CounterTask;

public interface CreateCounterTaskPort {

    CounterTask createTask(CounterTask counterTask);

}
