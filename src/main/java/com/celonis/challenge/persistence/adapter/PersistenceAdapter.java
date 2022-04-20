package com.celonis.challenge.persistence.adapter;

import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.domain.port.CreateTaskPort;
import com.celonis.challenge.domain.port.DeleteTaskPort;
import com.celonis.challenge.domain.port.ReadTaskPort;
import com.celonis.challenge.domain.port.UpdateTaskPort;
import com.celonis.challenge.persistence.entities.TaskEntity;
import com.celonis.challenge.persistence.mapper.TaskEntityMapper;
import com.celonis.challenge.persistence.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PersistenceAdapter implements CreateTaskPort, ReadTaskPort, DeleteTaskPort, UpdateTaskPort {

    private final CounterRepository repository;
    private final TaskEntityMapper mapper;

    @Override
    public Task createTask(Task task) {
        task.setId(null);
        task.setCreationDate(LocalDate.now());
        task.setLastExecution(LocalDateTime.MIN);
        TaskEntity entity = mapper.toEntity(task);
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
        TaskEntity entity = mapper.toEntity(existing);
        return mapper.toDomain(repository.save(entity));
    }

    public void updateExecution(Task task) {
        task.setLastExecution(LocalDateTime.now());
        TaskEntity entity = mapper.toEntity(task);
        mapper.toDomain(repository.save(entity));
    }

}
