package com.fran.task.persistence.adapter;

import com.fran.task.domain.exceptions.NotFoundException;
import com.fran.task.domain.model.Task;
import com.fran.task.domain.port.TaskPersistencePort;
import com.fran.task.persistence.entities.TaskDocument;
import com.fran.task.persistence.mapper.TaskDocumentMapper;
import com.fran.task.persistence.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PersistenceAdapter implements TaskPersistencePort {

    private final TaskRepository repository;
    private final TaskDocumentMapper mapper;

    @Override
    public Task createTask(Task task) {
        Task newTask = task.withCreationDate(LocalDateTime.now())
                .withStatus(com.fran.task.domain.model.TaskStatus.CREATED);
        TaskDocument saved = repository.save(mapper.toEntity(newTask));
        log.info("Creating task {}", saved.getId());
        return mapper.toDomain(saved);
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
        log.info("Updating task {}", taskId);
        Task existing = getTask(taskId).orElseThrow(NotFoundException::new);
        Task updated = existing.withBegin(taskUpdate.begin())
                .withFinish(taskUpdate.finish())
                .withName(taskUpdate.name());
        TaskDocument entity = mapper.toEntity(updated);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void updateExecution(Task task) {
        log.info("Updating execution for task {}", task.id());
        TaskDocument entity = mapper.toEntity(task.withLastExecution(LocalDateTime.now()));
        mapper.toDomain(repository.save(entity));
    }

}
