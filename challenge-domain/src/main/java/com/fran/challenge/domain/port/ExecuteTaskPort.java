package com.fran.challenge.domain.port;

import com.fran.challenge.domain.model.Task;

public interface ExecuteTaskPort {

    void executeTask(Task task);
}
