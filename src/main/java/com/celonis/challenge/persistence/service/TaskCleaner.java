package com.celonis.challenge.persistence.service;

import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.persistence.adapter.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Slf4j
@Service
public class TaskCleaner {

    private final PersistenceAdapter persistenceAdapter;

    @Scheduled(cron = "${delete.expired.task.scheduled}")
    public void cleanExpiredTask() {
        log.info("Begin Cleaning task ... ");
        persistenceAdapter.getTasks().stream()
                .filter(counterTask -> counterTask.getLastExecution().isBefore(LocalDateTime.now()))
                .map(Task::getId)
                .forEach(persistenceAdapter::deleteTask);

        log.info("Finished Cleaning task ... ");
    }

}
