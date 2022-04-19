package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.FileTask;

public interface CreateFileTaskPort {

    FileTask createTask(FileTask fileTask);

}
