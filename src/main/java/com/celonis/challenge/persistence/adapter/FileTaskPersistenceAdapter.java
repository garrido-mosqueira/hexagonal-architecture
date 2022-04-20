package com.celonis.challenge.persistence.adapter;

import com.celonis.challenge.domain.exceptions.NotFoundException;
import com.celonis.challenge.domain.model.FileTask;
import com.celonis.challenge.domain.port.CreateFileTaskPort;
import com.celonis.challenge.domain.port.DeleteFileTaskPort;
import com.celonis.challenge.domain.port.ReadFileTaskPort;
import com.celonis.challenge.domain.port.UpdateFileTaskPort;
import com.celonis.challenge.persistence.entities.FileTaskEntity;
import com.celonis.challenge.persistence.mapper.FileEntityMapper;
import com.celonis.challenge.persistence.repository.FileTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FileTaskPersistenceAdapter implements CreateFileTaskPort, ReadFileTaskPort, DeleteFileTaskPort, UpdateFileTaskPort {

    private final FileTaskRepository repository;
    private final FileEntityMapper mapper;

    @Override
    public FileTask createTask(FileTask fileTask) {
        fileTask.setId(null);
        fileTask.setCreationDate(LocalDate.now());
        fileTask.setLastExecution(LocalDateTime.MIN);
        FileTaskEntity entity = mapper.toEntity(fileTask);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<FileTask> getTasks() {
        return mapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<FileTask> getTask(String taskId) {
        return repository.findById(taskId)
                .map(mapper::toDomain)
                .or(Optional::empty);
    }

    @Override
    public void deleteTask(String taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public FileTask updateTask(String taskId, FileTask fileTaskUpdate) {
        FileTask existing = getTask(taskId).orElseThrow(NotFoundException::new);
        existing.setName(fileTaskUpdate.getName());
        FileTaskEntity entity = mapper.toEntity(existing);
        return mapper.toDomain(repository.save(entity));
    }

}
