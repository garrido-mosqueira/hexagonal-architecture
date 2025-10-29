package com.fran.task.domain.model;

import java.time.LocalDateTime;

public record Task(
        String id,
        String name,
        LocalDateTime creationDate,
        LocalDateTime lastExecution,
        Integer begin,
        Integer finish,
        Integer progress) {

    public Task withProgress(Integer newProgress) {
        return new Task(id, name, creationDate, lastExecution,
                begin, finish, newProgress);
    }

    public Task withCreationDate(LocalDateTime newCreationDate) {
        return new Task(id, name, newCreationDate, lastExecution,
                begin, finish, progress);
    }

    public Task withLastExecution(LocalDateTime newLastExecution) {
        return new Task(id, name, creationDate, newLastExecution,
                begin, finish, progress);
    }

    public Task withName(String newName) {
        return new Task(id, newName, creationDate, lastExecution,
                begin, finish, progress);
    }

    public Task withBegin(Integer newBegin) {
        return new Task(id, name, creationDate, lastExecution,
                newBegin, finish, progress);
    }

    public Task withFinish(Integer newFinish) {
        return new Task(id, name, creationDate, lastExecution,
                begin, newFinish, progress);
    }

}
