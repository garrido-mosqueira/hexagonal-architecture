package com.celonis.challenge.persistence.adapter;

import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.Task;
import com.celonis.challenge.domain.port.CreateTaskPort;
import com.celonis.challenge.domain.port.DeleteTaskPort;
import com.celonis.challenge.domain.port.ReadTaskPort;
import com.celonis.challenge.domain.port.UpdateTaskPort;
import com.celonis.challenge.persistence.entities.TaskDocument;
import com.celonis.challenge.persistence.mapper.TaskDocumentMapper;
import com.celonis.challenge.persistence.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PersistenceAdapter implements CreateTaskPort, ReadTaskPort, DeleteTaskPort, UpdateTaskPort {

    private final TaskRepository repository;
    private final TaskDocumentMapper mapper;

    @Override
    public Task createTask(Task task) {
        task.setId(null);
        task.setCreationDate(new Date(System.currentTimeMillis()));
        task.setLastExecution(new Date(System.currentTimeMillis()));
        TaskDocument entity = mapper.toEntity(task);
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
        TaskDocument entity = mapper.toEntity(existing);
        return mapper.toDomain(repository.save(entity));
    }

    public void updateExecution(Task task) {
        task.setLastExecution(new Date(System.currentTimeMillis()));
        TaskDocument entity = mapper.toEntity(task);
        mapper.toDomain(repository.save(entity));
    }

}
