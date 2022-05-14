package com.fran.challenge.persistence.service;

import com.fran.challenge.domain.model.Task;
import com.fran.challenge.persistence.adapter.PersistenceAdapter;
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
                .filter(counterTask -> counterTask.getLastExecution().before(new Date(System.currentTimeMillis() - 180 * 1000)))
                .map(Task::getId)
                .forEach(persistenceAdapter::deleteTask);

        log.info("Finished Cleaning task ... ");
    }

}
