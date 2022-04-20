package com.celonis.challenge.tasks.counter.adapter;

import com.celonis.challenge.domain.exceptions.InternalException;
import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.port.*;
import com.celonis.challenge.persistence.adapter.CounterPersistenceAdapter;
import com.celonis.challenge.tasks.counter.mapper.CounterMapper;
import com.celonis.challenge.tasks.counter.model.Counter;
import com.celonis.challenge.tasks.counter.service.CounterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CounterTaskAdapter
        implements CreateCounterTaskPort, ReadCounterTaskPort, UpdateCounterTaskPort, DeleteCounterTaskPort, CancelCounterTaskPort, ExecuteCounterTaskPort {

    private final CounterPersistenceAdapter persistenceAdapter;
    private final CounterService counterService;
    private final CounterMapper mapper;

    @Override
    public CounterTask createTask(CounterTask counterTask) {
        return persistenceAdapter.createTask(counterTask);
    }

    @Override
    public List<CounterTask> getTasks() {
        return persistenceAdapter.getTasks();
    }

    @Override
    public Optional<CounterTask> getTask(String taskId) {
        return persistenceAdapter.getTask(taskId);
    }

    @Override
    public CounterTask updateTask(String taskId, CounterTask counterTaskUpdate) {
        return persistenceAdapter.updateTask(taskId, counterTaskUpdate);
    }

    @Override
    public void deleteTask(String taskId) {
        persistenceAdapter.deleteTask(taskId);
    }

    @Override
    public void cancelTask(String taskId) {
        counterService.cancelCounter(taskId);
    }

    public List<CounterTask> getAllRunningCounters() {
        return mapper.toDomain(counterService.getAllRunningCounters());
    }

    @SneakyThrows
    public CounterTask getRunningCounter(final String counterId) {
        return mapper.toDomain(counterService.getRunningCounter(counterId));
    }

    @SneakyThrows
    @Override
    public void executeTask(CounterTask fileTask) {
        if (fileTask.getStorageLocation() != null) {
            executeFileTask(fileTask);
        } else {
            executeCounterTask(fileTask);
        }
    }

    public void executeFileTask(CounterTask fileTask) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new InternalException("Zip file not found");
        }
        try {
            storeResult(fileTask, url);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    public void executeCounterTask(CounterTask task) throws SchedulerException {
        Counter counter = mapper.toCounter(task);
        counterService.runCounterJob(counter);
        persistenceAdapter.updateExecution(task);
    }

    public File getTaskResult(String taskId) {
        CounterTask task = persistenceAdapter.getTask(taskId).orElseThrow(NotFoundException::new);
        File inputFile = new File(task.getStorageLocation());

        if (!inputFile.exists()) {
            throw new InternalException("File not generated yet");
        }
        return inputFile;
    }

    public void storeResult(CounterTask task, URL url) throws IOException {
        File outputFile = File.createTempFile(task.getId(), ".zip");
        outputFile.deleteOnExit();
        task.setStorageLocation(outputFile.getAbsolutePath());
        persistenceAdapter.createTask(task);
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }

}
