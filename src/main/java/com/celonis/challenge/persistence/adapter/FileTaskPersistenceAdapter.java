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
import java.util.List;

@RequiredArgsConstructor
@Component
public class FileTaskPersistenceAdapter implements CreateFileTaskPort, ReadFileTaskPort, DeleteFileTaskPort, UpdateFileTaskPort {

    private final FileTaskRepository repository;
    private final FileEntityMapper mapper;

    @Override
    public FileTask createTask(FileTask counterTask) {
        counterTask.setId(null);
        counterTask.setCreationDate(LocalDate.now());
        FileTaskEntity entity = mapper.toEntity(counterTask);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<FileTask> getTasks() {
        return mapper.toDomain(repository.findAll());
    }

    @Override
    public FileTask getTask(String taskId) {
        return mapper.toDomain(repository.findById(taskId).orElseThrow(NotFoundException::new));
    }

    @Override
    public void deleteTask(String taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public FileTask updateTask(String taskId, FileTask counterTaskUpdate) {
        FileTask existing = getTask(taskId);
        existing.setName(counterTaskUpdate.getName());
        FileTaskEntity entity = mapper.toEntity(existing);
        return mapper.toDomain(repository.save(entity));
    }

}
