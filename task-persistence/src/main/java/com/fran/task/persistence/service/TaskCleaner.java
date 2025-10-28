package com.fran.task.persistence.service;

import com.fran.task.domain.model.Task;
import com.fran.task.persistence.adapter.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Service
public class TaskCleaner {

    private final PersistenceAdapter persistenceAdapter;

    @Scheduled(cron = "${delete.expired.task.scheduled}")
    public void cleanExpiredTask() {
        log.info("Begin Cleaning task ... ");
        persistenceAdapter.getTasks().stream()
                .filter(counterTask ->
                        counterTask.lastExecution() == null && counterTask.creationDate().before(new Date(System.currentTimeMillis() - 180 * 1000))
                                || counterTask.lastExecution() != null && counterTask.lastExecution().before(new Date(System.currentTimeMillis() - 180 * 1000)))
                .map(Task::id)
                .forEach(persistenceAdapter::deleteTask);

        log.info("Finished Cleaning task ... ");
    }

}
