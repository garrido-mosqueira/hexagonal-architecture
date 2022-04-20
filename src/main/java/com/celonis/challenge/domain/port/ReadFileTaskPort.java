package com.celonis.challenge.domain.port;

import com.celonis.challenge.domain.model.FileTask;

import java.util.List;
import java.util.Optional;

public interface ReadFileTaskPort {

    Optional<FileTask> getTask(String taskId);

    List<FileTask> getTasks();

}
