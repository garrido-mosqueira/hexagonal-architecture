package com.fran.task.domain.model;

import java.time.LocalDateTime;

public record Task(
        String id,
        String name,
        LocalDateTime creationDate,
        LocalDateTime lastExecution,
        Integer begin,
        Integer finish,
        Integer progress,
        String storageLocation) {

    public Task withProgress(Integer newProgress) {
        return new Task(id, name, creationDate, lastExecution,
                begin, finish, newProgress, storageLocation);
    }

    public Task withCreationDate(LocalDateTime newCreationDate) {
        return new Task(id, name, newCreationDate, lastExecution,
                begin, finish, progress, storageLocation);
    }

    public Task withLastExecution(LocalDateTime newLastExecution) {
        return new Task(id, name, creationDate, newLastExecution,
                begin, finish, progress, storageLocation);
    }

    public Task withName(String newName){
        return new Task(id, newName, creationDate, lastExecution,
                begin, finish, progress, storageLocation);
    }

    public Task withBegin(Integer newBegin) {
        return new Task(id, name, creationDate, lastExecution,
                newBegin, finish, progress, storageLocation);
    }

    public Task withFinish(Integer newFinish) {
        return new Task(id, name, creationDate, lastExecution,
                begin, newFinish, progress, storageLocation);
    }

}
