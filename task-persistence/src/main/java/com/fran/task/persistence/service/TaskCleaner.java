package com.fran.task.persistence.service;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
public class TaskCleaner {

    private final TaskPersistencePort persistencePort;

    public void cleanExpiredTask() {
        log.info("Begin Cleaning task ... ");
        persistencePort.getTasks().stream()
                .filter(counterTask ->
                        counterTask.lastExecution() == null && counterTask.creationDate().isBefore(LocalDateTime.now().minusMinutes(3))
                                || counterTask.lastExecution() != null && counterTask.lastExecution().isBefore(LocalDateTime.now().minusMinutes(3)))
                .map(Task::id)
                .forEach(persistencePort::deleteTask);

        log.info("Finished Cleaning task ... ");
    }
}
