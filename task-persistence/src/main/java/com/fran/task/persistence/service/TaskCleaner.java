package com.fran.task.persistence.service;

import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class TaskCleaner {

    private final TaskPersistencePort persistencePort;
    private static final Logger log = LoggerFactory.getLogger(TaskCleaner.class);

    public TaskCleaner(TaskPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

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
