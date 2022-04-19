package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.FileTask;

import java.util.List;

public interface ReadFileTaskPort {

    FileTask getTask(String taskId);

    List<FileTask> getTasks();

}
