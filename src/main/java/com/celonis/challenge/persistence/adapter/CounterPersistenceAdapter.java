package com.celonis.challenge.persistence.adapter;

import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.domain.port.CreateCounterTaskPort;
import com.celonis.challenge.domain.port.DeleteCounterTaskPort;
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
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CounterPersistenceAdapter implements CreateCounterTaskPort, ReadCounterTaskPort, DeleteCounterTaskPort, UpdateCounterTaskPort {

    private final CounterRepository repository;
    private final CounterEntityMapper mapper;

    @Override
    public Task createTask(Task task) {
        task.setId(null);
        task.setCreationDate(LocalDate.now());
        task.setLastExecution(LocalDateTime.MIN);
        CounterEntity entity = mapper.toEntity(task);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Task> getTasks() {
        return mapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Task> getTask(String taskId) {
        return repository.findById(taskId)
                .map(mapper::toDomain)
                .or(Optional::empty);
    }

    @Override
    public void deleteTask(String taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public Task updateTask(String taskId, Task taskUpdate) {
        Task existing = getTask(taskId).orElseThrow(NotFoundException::new);
        existing.setBegin(taskUpdate.getBegin());
        existing.setFinish(taskUpdate.getFinish());
        existing.setName(taskUpdate.getName());
        CounterEntity entity = mapper.toEntity(existing);
        return mapper.toDomain(repository.save(entity));
    }

    public void updateExecution(Task task) {
        task.setLastExecution(LocalDateTime.now());
        CounterEntity entity = mapper.toEntity(task);
        mapper.toDomain(repository.save(entity));
    }

}
