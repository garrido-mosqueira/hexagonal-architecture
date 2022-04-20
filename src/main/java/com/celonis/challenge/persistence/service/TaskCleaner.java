package com.celonis.challenge.persistence.service;

import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.model.FileTask;
import com.celonis.challenge.persistence.adapter.CounterPersistenceAdapter;
import com.celonis.challenge.persistence.adapter.FileTaskPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Slf4j
@Service
public class TaskCleaner {

    private final CounterPersistenceAdapter counterPersistenceAdapter;
    private final FileTaskPersistenceAdapter fileTaskPersistenceAdapter;

    @Scheduled(cron = "${delete.expired.task.scheduled}")
    public void cleanExpiredTask() {
        log.info("Begin Cleaning task ... ");
        counterPersistenceAdapter.getTasks().stream()
                .filter(counterTask -> counterTask.getLastExecution().isBefore(LocalDateTime.now()))
                .map(CounterTask::getId)
                .forEach(counterPersistenceAdapter::deleteTask);

        fileTaskPersistenceAdapter.getTasks().stream()
                .filter(counterTask -> counterTask.getLastExecution().isBefore(LocalDateTime.now()))
                .map(FileTask::getId)
                .forEach(counterPersistenceAdapter::deleteTask);
        log.info("Finished Cleaning task ... ");
    }

}
