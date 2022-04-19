package com.celonis.challenge.persistence.adapter;

import com.celonis.challenge.api.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.CounterTask;
import com.celonis.challenge.domain.port.CreateCounterTaskPort;
import com.celonis.challenge.domain.port.DeleteTaskPort;
import com.celonis.challenge.domain.port.ReadCounterTaskPort;
import com.celonis.challenge.domain.port.UpdateCounterTaskPort;
import com.celonis.challenge.persistence.entities.CounterEntity;
import com.celonis.challenge.persistence.mapper.CounterEntityMapper;
import com.celonis.challenge.persistence.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CounterPersistenceAdapter implements CreateCounterTaskPort, ReadCounterTaskPort, DeleteTaskPort, UpdateCounterTaskPort {

    private final CounterRepository repository;
    private final CounterEntityMapper mapper;

    @Override
    public CounterTask createTask(CounterTask counterTask) {
        counterTask.setId(null);
        counterTask.setCreationDate(LocalDate.now());
        CounterEntity entity = mapper.toEntity(counterTask);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<CounterTask> getTasks() {
        return mapper.toDomain(repository.findAll());
    }

    @Override
    public CounterTask getTask(String taskId) {
        return mapper.toDomain(repository.findById(taskId).orElseThrow(NotFoundException::new));
    }

    @Override
    public void deleteTask(String taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public CounterTask updateTask(String taskId, CounterTask counterTaskUpdate) {
        CounterTask existing = getTask(taskId);
        existing.setBegin(counterTaskUpdate.getBegin());
        existing.setFinish(counterTaskUpdate.getFinish());
        existing.setName(counterTaskUpdate.getName());
        CounterEntity entity = mapper.toEntity(existing);
        return mapper.toDomain(repository.save(entity));
    }

    public CounterTask updateExecution(String taskId) {
        CounterTask existing = getTask(taskId);
        existing.setLastExecution(LocalDateTime.now());
        CounterEntity entity = mapper.toEntity(existing);
        return mapper.toDomain(repository.save(entity));
    }

}
