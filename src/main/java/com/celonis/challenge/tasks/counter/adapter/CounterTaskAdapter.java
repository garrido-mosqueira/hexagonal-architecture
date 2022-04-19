package com.celonis.challenge.tasks.counter.adapter;

import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.port.*;
import com.celonis.challenge.persistence.adapter.CounterPersistenceAdapter;
import com.celonis.challenge.tasks.counter.mapper.CounterMapper;
import com.celonis.challenge.tasks.counter.model.Counter;
import com.celonis.challenge.tasks.counter.service.CounterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CounterTaskAdapter
        implements ReadCounterTaskPort, CreateCounterTaskPort, UpdateCounterTaskPort, CancelCounterTaskPort, ExecuteCounterTaskPort {

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
    public CounterTask getTask(String taskId) {
        return persistenceAdapter.getTask(taskId);
    }

    @Override
    public CounterTask updateTask(String taskId, CounterTask counterTaskUpdate) {
        return persistenceAdapter.updateTask(taskId, counterTaskUpdate);
    }

    @Override
    public void cancelTask(String taskId) {
        counterService.cancelCounter(taskId);
        persistenceAdapter.deleteTask(taskId);
    }

    @SneakyThrows
    @Override
    public void executeTask(String taskId) {
        CounterTask task = persistenceAdapter.getTask(taskId);
        Counter counter = mapper.toCounter(task);
        counterService.runCounterJob(counter);
        persistenceAdapter.updateExecution(taskId);
    }

    public List<CounterTask> getAllRunningCounters() {
        return mapper.toDomain(counterService.getAllRunningCounters());
    }

    @SneakyThrows
    public CounterTask getRunningCounter(final String counterId) {
        return mapper.toDomain(counterService.getRunningCounter(counterId));
    }

}
