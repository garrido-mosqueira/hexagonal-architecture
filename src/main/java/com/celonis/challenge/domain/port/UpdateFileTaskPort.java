package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.FileTask;

public interface UpdateFileTaskPort {

    FileTask updateTask(String taskId, FileTask counterTaskUpdate);

}
