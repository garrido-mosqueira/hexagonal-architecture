package com.fran.task.domain.model;

import java.time.LocalDateTime;

public record Task(
        String id,
        String name,
        TaskType taskType,
        LocalDateTime creationDate,
        LocalDateTime lastExecution,
        Integer begin,
        Integer finish,
        Integer progress,
        TaskStatus status) {

    public Task {
        if (begin != null && finish != null && begin > finish) {
            throw new IllegalArgumentException("Begin value cannot be greater than finish value");
        }
    }

    public Task withProgress(Integer newProgress) {
        return new Task(id, name, taskType, creationDate, lastExecution,
                begin, finish, newProgress, status);
    }

    public Task withStatus(TaskStatus newStatus) {
        return new Task(id, name, taskType, creationDate, lastExecution,
                begin, finish, progress, newStatus);
    }

    public Task withCreationDate(LocalDateTime newCreationDate) {
        return new Task(id, name, taskType, newCreationDate, lastExecution,
                begin, finish, progress, status);
    }

    public Task withLastExecution(LocalDateTime newLastExecution) {
        return new Task(id, name, taskType, creationDate, newLastExecution,
                begin, finish, progress, status);
    }

    public Task withName(String newName) {
        return new Task(id, newName, taskType, creationDate, lastExecution,
                begin, finish, progress, status);
    }

    public Task withBegin(Integer newBegin) {
        return new Task(id, name, taskType, creationDate, lastExecution,
                newBegin, finish, progress, status);
    }

    public Task withFinish(Integer newFinish) {
        return new Task(id, name, taskType, creationDate, lastExecution,
                begin, newFinish, progress, status);
    }

}
